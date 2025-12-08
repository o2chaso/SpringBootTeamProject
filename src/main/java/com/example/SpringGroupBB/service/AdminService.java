package com.example.SpringGroupBB.service;

import com.example.SpringGroupBB.dto.ComplaintDTO;
import com.example.SpringGroupBB.entity.Board;
import com.example.SpringGroupBB.entity.BoardReply;
import com.example.SpringGroupBB.entity.Complaint;
import com.example.SpringGroupBB.repository.BoardReplyRepository;
import com.example.SpringGroupBB.repository.BoardRepository;
import com.example.SpringGroupBB.repository.ComplaintRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {

  private final ComplaintRepository complaintRepository;
  private final BoardRepository boardRepository;
  private final BoardReplyRepository boardReplyRepository;

  public Complaint getComplaintSearch(int partId) {
    return complaintRepository
            .findTop1ByPartIdOrderByIdDesc(partId)
            .orElseThrow( () -> new IllegalStateException("신고 내역을 찾을 수 없습니다."));
  }

  public int setBoardComplaintInput(ComplaintDTO dto) {
    try {
      Complaint complaint = dto.entityToDto();


      complaintRepository.save(complaint);

      return 1;

    } catch (Exception e) {

      return 0;

    }

  }

  public void setBoardTableComplaintOk(Long partId) {
    Board board = boardRepository.findById(partId)
            .orElseThrow(() -> new IllegalStateException("게시글을 찾을 수 없습니다."));

    board.setComplaint("OK");
  }

  public int setComplaintDelete(long partId, String part) {
    try {
      switch (part) {
        case "board":
          deleteBoardComplaintDelete((int) partId);
          break;
        case "board_reply":
          deleteBoardReplyComplaintDelete((int) partId);
          break;
        default:
          throw new IllegalArgumentException("지원하지 않는 part 값:" + part);
      }
      return 1;
    } catch (Exception e) {
      return 0;
    }
  }

  private void deleteBoardComplaintDelete(int partId) {
    Board board = boardRepository.findById((long) partId)
            .orElseThrow( () -> new IllegalStateException("게시글을 찾을 수 없습니다."));

    board.setComplaint("DE");
  }

  private void deleteBoardReplyComplaintDelete(int partId) {
    BoardReply boardReply = boardReplyRepository.findById((long) partId)
            .orElseThrow( () -> new IllegalStateException("댓글을 찾을 수 없습니다."));

    boardReply.setComplaint("DE");
  }

  public int setComplaintProcess(long partId, String flag) {
    try {
      Board board = boardRepository.findById(partId)
              .orElseThrow( () -> new IllegalStateException("게시글을 찾을 수 없습니다."));

      board.setComplaint(flag);

      return 1;
    } catch (Exception e) {
      return 0;
    }
  }

  public void setComplaintProcessOk(Long id, String complaintSw) {
    Complaint complaint = complaintRepository.findById(id)
            .orElseThrow( () -> new IllegalStateException("신고 내역을 찾을 수 없습니다."));

    complaint.setProgress(complaintSw);
  }

}
