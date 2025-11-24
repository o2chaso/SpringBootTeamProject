package com.example.SpringGroupBB.service;

import com.example.SpringGroupBB.entity.Board;
import com.example.SpringGroupBB.entity.BoardReply;
import com.example.SpringGroupBB.repository.BoardReplyRepository;
import com.example.SpringGroupBB.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {

  private final BoardRepository boardRepository;
  private final BoardReplyRepository boardReplyRepository;

//  public List<Board> getBoardList() {
//    return boardRepository.findAll();
//  }

  public Board setBoardInput(Board board) {
    return boardRepository.save(board);
  }

  public Board getBoardContent(Long id) {
    //return boardRepository.findById(id).get();
    return boardRepository.findById(id).orElse(null);
  }

  public void setBoardReadNumPlus(Long id) {
    boardRepository.setBoardReadNumPlus(id);
  }

  public Board getPreNextSearch(Long id, String preNext) {
    if(preNext.equals("pre")) {
      return boardRepository.findPrevious(id);
    }
    else {
      return boardRepository.findNext(id);
    }
  }

  public List<BoardReply> getBoardReply(Long boardId) {
    return boardReplyRepository.findByBoardIdOrderById(boardId);
  }

  public void setBoardReplyInput(BoardReply boardReply) {
    boardReplyRepository.save(boardReply);
  }

  public boolean setBoardImageDelete(String content, String realPath) {
    //             0         1         2         3         4         5
    //             012345678901234567890123456789012345678901234567890
    // <img alt="" src="/ckeditorUpload/250916121142_4.jpg" style="height:402px; width:600px" />

    int position = 21;
    String nextImg = content.substring(content.indexOf("src=\"/") + position);
    boolean sw = true, flag = false;

    while(sw) {
      String imgFile = nextImg.substring(0, nextImg.indexOf("\""));
      String origFilePath = realPath + imgFile;

      File delFile = new File(origFilePath);
      if(delFile.exists()) delFile.delete();

      if(nextImg.indexOf("src=\"/") == -1) sw = false;
      else nextImg = nextImg.substring(nextImg.indexOf("src=\"/") + position);

      flag = true;
    }
    return flag;
  }

}
