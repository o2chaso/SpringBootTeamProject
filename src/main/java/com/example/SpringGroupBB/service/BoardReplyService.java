package com.example.SpringGroupBB.service;

import com.example.SpringGroupBB.entity.BoardReply;
import com.example.SpringGroupBB.repository.BoardReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardReplyService {
  private final BoardReplyRepository boardReplyRepository;

  public List<BoardReply> selectBoardReplyBoardID(Long id) {
    try {
      return boardReplyRepository.findByBoardId(id).map(Collections::singletonList).orElse(Collections.emptyList());
    } catch (Exception e) {return Collections.emptyList();}
  }
}
