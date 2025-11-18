package com.example.SpringGroupBB.controller;

import com.example.SpringGroupBB.common.Pagination;
import com.example.SpringGroupBB.constant.Progress;
import com.example.SpringGroupBB.dto.PageDTO;
import com.example.SpringGroupBB.dto.QnADTO;
import com.example.SpringGroupBB.entity.QnA;
import com.example.SpringGroupBB.service.MemberService;
import com.example.SpringGroupBB.service.QnAService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/qna")
public class QnAController {
  private final QnAService qnaService;
  private final MemberService memberService;
  private final Pagination pagination;

  @GetMapping("/qnaList")
  public String qnaListGet(Authentication authentication, Model model, PageDTO pageDTO) {
    pageDTO.setSection("qna");
    pageDTO = pagination.pagination(authentication, pageDTO);

    model.addAttribute("pageDTO", pageDTO);
    model.addAttribute("userCsrf", true);
    return "qna/qnaList";
  }

  @GetMapping("/qnaInput")
  public String qnaInputGet(Authentication authentication, Model model) {
    model.addAttribute("dto", memberService.selectMemberMid(authentication.getName()));
    model.addAttribute("userCsrf", true);
    return "qna/qnaInput";
  }
  @ResponseBody
  @PostMapping("qnaValid")
  public String[] qnaValide(@Valid QnADTO dto, BindingResult bindingResult) {
    if(bindingResult.hasErrors()) {
      String[] error = new String[bindingResult.getAllErrors().size()];
      for(int i=0; i<bindingResult.getAllErrors().size(); i++) {
        error[i] = bindingResult.getAllErrors().get(i).getDefaultMessage();
        System.out.println(error[i]);
      }
      return error;
    }
    else return new String[] {""};
  }
  @PostMapping("/qnaInput")
  public String qnaInputPost(RedirectAttributes rttr, QnADTO dto) {
    if(dto.getFromMid().isEmpty() || dto.getDearMid().isEmpty() || dto.getTitle().length()>20 || dto.getTitle().isEmpty() || dto.getContent().isEmpty()) {
      rttr.addFlashAttribute("message", "잘못된 접근입니다.");
      return "redirect:/";
    }
    try {
      qnaService.insertQnA(dto);
    } catch (Exception e) {
      rttr.addFlashAttribute("message", "1:1문의에 실패했습니다.\n잠시 후, 다시 시도해주세요.");
      return "redirect:/qna/qnaList";
    }
    rttr.addFlashAttribute("message", "문의가 정상적으로 등록되었습니다.");
    return "redirect:/qna/qnaList";
  }

  @GetMapping("/qnaAnswer")
  public String qnaAnswerGet(Authentication authentication, Model model, PageDTO pageDTO) {
    pageDTO.setSection("qnaAdmin");
    pageDTO = pagination.pagination(authentication, pageDTO);

    model.addAttribute("pageDTO", pageDTO);
    model.addAttribute("userCsrf", true);
    return "qna/qnaAnswer";
  }
  @ResponseBody
  @PostMapping("/qnaAnswer")
  public int qnaAnswerPost(Long id, String content) {
    try {
      qnaService.insertQnAAnswer(id, content);
    } catch (Exception e) {
      return 0;
    }
    return 1;
  }

  @GetMapping("/qnaNewWindow/{id}")
  public String qnaNewWindowGet(Authentication authentication, Model model,
                                @PathVariable Long id) {
    List<QnA> qnaList = qnaService.selectQnAParentId(id);

    if(qnaList.size() > 1) {
      if(!qnaList.getLast().getFromMid().getMid().equals(authentication.getName())) qnaService.updateOpenSWOK(qnaList.getFirst());
      else qnaService.updateOpenSWNO(qnaList.getFirst());
    }
    else if(qnaList.size() == 1) {
      if(!qnaList.getFirst().getFromMid().getMid().equals(authentication.getName()))  qnaService.updateOpenSWOK(qnaList.getFirst());
      else qnaService.updateOpenSWNO(qnaList.getFirst());
    }
    model.addAttribute("qnaList", qnaList);
    model.addAttribute("id", id);
    model.addAttribute("progress", qnaService.selectQnAId(id).getProgress());
    return "qna/qnaNewWindow";
  }
  @ResponseBody
  @PostMapping("/qnaNewWindow")
  public Long qnaNewWindowPost(Authentication authentication, Long id) {
    List<QnA> qnaList = qnaService.selectQnAParentId(id);
    if(qnaList.size() > 1) {
      if(!qnaList.getLast().getFromMid().getMid().equals(authentication.getName())) return qnaList.getLast().getId();
      else return 0L;
    }
    else if(qnaList.size() == 0) return -1L;
    else {
      if(!qnaList.getFirst().getFromMid().getMid().equals(authentication.getName())) return qnaList.getFirst().getId();
      else return 0L;
    }
  }

  @ResponseBody
  @PostMapping("/qnaProgressChange")
  public int qnaProgressChangePost(Long id, String setProgress) {
    Progress progress;
    if(setProgress.equals("RESOLVING")) progress = Progress.RESOLVING;
    else if(setProgress.equals("RESOLVED")) progress = Progress.RESOLVED;
    else if(setProgress.equals("UNRESOLVABLE")) progress = Progress.UNRESOLVABLE;
    else if(setProgress.equals("DELETE")) progress = Progress.DELETE;
    else return -1;

    try {
      if(progress == Progress.DELETE) qnaService.deleteQnA(id);
      else qnaService.updateProgress(id, progress);
    } catch (Exception e) {
      return 0;
    }
    return 1;
  }
}
