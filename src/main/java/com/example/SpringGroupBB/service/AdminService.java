package com.example.SpringGroupBB.service;

import com.example.SpringGroupBB.dto.ComplaintDTO;
import com.example.SpringGroupBB.entity.Board;
import com.example.SpringGroupBB.entity.BoardReply;
import com.example.SpringGroupBB.entity.Complaint;
import com.example.SpringGroupBB.repository.BoardReplyRepository;
import com.example.SpringGroupBB.repository.BoardRepository;
import com.example.SpringGroupBB.repository.ComplaintRepository;
import groovy.util.logging.Slf4j;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AdminService {


  private final ComplaintRepository complaintRepository;
  private final BoardRepository boardRepository;
  private final BoardReplyRepository boardReplyRepository;

  public Complaint getComplaintSearch(int partId) {
    return complaintRepository
            .findTop1ByPartIdOrderByIdDesc(partId)
            .orElseThrow( () -> new IllegalStateException("신고 내역을 찾을 수 없습니다."));
  }

  // 수정: 먼저 모든 데이터 검증 후 저장
  @Transactional
  public int setBoardComplaintInput(ComplaintDTO dto) {
    try {
      // 먼저 게시물 존재 여부 확인
      Board board = boardRepository.findById(dto.getPartId())
              .orElseThrow(() -> new IllegalStateException("게시글을 찾을 수 없습니다."));

      // 신고 저장
      Complaint complaint = Complaint.dtoToEntity(dto);
      complaintRepository.save(complaint);

      // 게시물 상태 업데이트
      board.setComplaint("OK");

      return 1;
    } catch (IllegalStateException e) {
      return 0;
    } catch (Exception e) {
      return 0;
    }
  }

  @Transactional
  public void setBoardTableComplaintOk(Long partId) {
    try {
      Board board = boardRepository.findById(partId)
              .orElseThrow(() -> new IllegalStateException("게시글을 찾을 수 없습니다."));
      board.setComplaint("OK");
    } catch (Exception e) {
    }
  }

  // 신고 삭제 처리
  @Transactional
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

  // 신고 처리 (H: 감추기)
  @Transactional
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

  // 신고 진행 상황 업데이트 (S: 해제, D: 삭제, H: 감추기)
  @Transactional
  public int setComplaintProcessOk(Long id, String complaintSw) {
    try {
      // 신고 내역 조회
      Complaint complaint = complaintRepository.findById(id)
              .orElseThrow( () -> new IllegalStateException("신고 내역을 찾을 수 없습니다."));

      // 게시물 조회 (먼저 확인!)
      Board board = boardRepository.findById(complaint.getPartId())
              .orElseThrow(() -> new IllegalStateException("게시글을 찾을 수 없습니다."));

      // 신고 진행 상황 업데이트
      complaint.setProgress(complaintSw);

      // 게시물 상태 업데이트
      if("S".equals(complaintSw)) {
        complaint.setProgress("처리완료(S)");
        board.setComplaint("OK");  // 신고 해제
      } else if("D".equals(complaintSw)) {
        complaint.setProgress("처리완료(D)");
        board.setComplaint("DE");  // 글 삭제
      } else if("H".equals(complaintSw)) {
        complaint.setProgress("처리중(H)");
        board.setComplaint("HI");  // 감추기
      }

      return 1;
    } catch (IllegalStateException e) {
      return 0;
    } catch (Exception e) {
      return 0;
    }
  }

}