package com.example.SpringGroupBB.controller;

import com.example.SpringGroupBB.dto.BoardDTO;
import com.example.SpringGroupBB.dto.ProductDTO;
import com.example.SpringGroupBB.dto.QnADTO;
import com.example.SpringGroupBB.service.BoardService;
import com.example.SpringGroupBB.service.ProductService;
import com.example.SpringGroupBB.service.QnAService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class SearchController {
  private final BoardService boardService;
  private final QnAService qnaService;
  private final ProductService productService;

  @GetMapping("/search")
  public String searchGet(Model model, Authentication authentication, String searchStr) {
    if(searchStr.replace(" ", "").equals("")) return "redirect:/";
    List<ProductDTO> productList = productService.selectSearchStr(searchStr);
    List<BoardDTO> boardList = boardService.selectSearchStr(searchStr);
    List<QnADTO> qnaList = qnaService.selectSearchStr(authentication.getName(), searchStr);

    // view단에 보내기 전에 정리.
    int searchLength = searchStr.length();
    productList.forEach(product -> {
      // 센서이름 강조.
      if(product.getSensorName().contains(searchStr)) product.setSensorName(product.getSensorName().replace(searchStr, "<span style='background-color:yellow; color:red'>"+searchStr+"</span>"));
      // 센서 모델명 강조.
      if(product.getModel().contains(searchStr)) product.setModel(product.getModel().replace(searchStr, "<span style='background-color:yellow; color:red'>"+searchStr+"</span>"));
      // 센서 타입 강조.
      if(product.getSensorType().contains(searchStr)) product.setSensorType(product.getSensorType().replace(searchStr, "<span style='background-color:yellow; color:red'>"+searchStr+"</span>"));
      // 센서 한 줄 설명 강조 및 길이 조절(검색단어 + 전후5).
      if(product.getShortDescription().contains(searchStr)) {
        if(product.getShortDescription().substring(0,product.getShortDescription().indexOf(searchStr)).length() >= searchLength+5) product.setShortDescription(product.getShortDescription().substring(product.getShortDescription().indexOf(searchStr)-5));
        else product.setShortDescription(product.getShortDescription().substring(product.getShortDescription().indexOf(searchStr)));
      }
      if(product.getShortDescription().length() >= searchLength+10) product.setShortDescription(product.getShortDescription().substring(0,searchLength+10));
      if(product.getShortDescription().contains(searchStr)) product.setShortDescription(product.getShortDescription().replace(searchStr, "<span style='background-color:yellow; color:red'>"+searchStr+"</span>"));
      // 센서 설명 강조 및 길이 조절(검색단어 + 전후5).
      if(product.getFeatures().contains(searchStr)) {
        if(product.getFeatures().substring(0,product.getFeatures().indexOf(searchStr)).length() >= searchLength+5) product.setFeatures(product.getFeatures().substring(product.getFeatures().indexOf(searchStr)-5));
        else product.setFeatures(product.getFeatures().substring(product.getFeatures().indexOf(searchStr)));
      }
      if(product.getFeatures().length() >= searchLength+10) product.setFeatures(product.getFeatures().substring(0,searchLength+10));
      if(product.getFeatures().contains(searchStr)) product.setFeatures(product.getFeatures().replace(searchStr, "<span style='background-color:yellow; color:red'>"+searchStr+"</span>"));
      // 센서 제조사 강조.
      if(product.getManufacturer().contains(searchStr)) product.setManufacturer(product.getManufacturer().replace(searchStr, "<span style='background-color:yellow; color:red'>"+searchStr+"</span>"));
    });

    boardList.forEach(board -> {
      // 게시글 제목 강조.
      if(board.getTitle().contains(searchStr)) board.setTitle(board.getTitle().replace(searchStr, "<span style='background-color:yellow; color:red'>"+searchStr+"</span>"));
      // 게시글 내용 강조 및 길이 조절(검색단어 + 전후5).
      if(board.getContent().contains(searchStr)) {
        if(board.getContent().substring(0,board.getContent().indexOf(searchStr)).length() >= searchLength+5) board.setContent(board.getContent().substring(board.getContent().indexOf(searchStr)-5));
        else board.setContent(board.getContent().substring(board.getContent().indexOf(searchStr)));
      }
      if(board.getContent().length() >= searchLength+10) board.setContent(board.getContent().substring(0, searchLength+10));
      if(board.getContent().contains(searchStr)) board.setContent(board.getContent().replace(searchStr, "<span style='background-color:yellow; color:red'>"+searchStr+"</span>"));
    });

    qnaList.forEach(qna -> {
      // 문의글 제목 강조.
      if(qna.getTitle().contains(searchStr)) qna.setTitle(qna.getTitle().replace(searchStr, "<span style='background-color:yellow; color:red'>"+searchStr+"</span>"));
      // 문의 내용 강조 및 길이 조절(검색단어 + 전후5).
      if(qna.getContent().contains(searchStr)) {
        if(qna.getContent().substring(0,qna.getContent().indexOf(searchStr)).length() >= searchLength+5) qna.setContent(qna.getContent().substring(qna.getContent().indexOf(searchStr)-5));
        else qna.setContent(qna.getContent().substring(qna.getContent().indexOf(searchStr)));
      }
      if(qna.getContent().length() >= searchLength+10) qna.setContent(qna.getContent().substring(0, searchLength+10));
      if(qna.getContent().contains(searchStr)) qna.setContent(qna.getContent().replace(searchStr, "<span style='background-color:yellow; color:red'>"+searchStr+"</span>"));
    });

    model.addAttribute("productList", productList);
    model.addAttribute("boardList", boardList);
    model.addAttribute("qnaList", qnaList);
    model.addAttribute("searchStr", searchStr);
    return "search/searchResult";
  }
}
