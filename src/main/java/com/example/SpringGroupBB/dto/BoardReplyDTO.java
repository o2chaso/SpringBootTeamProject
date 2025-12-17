package com.example.SpringGroupBB.dto;

import com.example.SpringGroupBB.entity.Board;
import com.example.SpringGroupBB.entity.BoardReply;
import com.example.SpringGroupBB.entity.Member;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardReplyDTO {
  private Long id;

  private Board board;

  private BoardReply parent; //부모 댓글

  private int ref;  // 원본글 번호(그룹번호)

  private int reStep; // 댓글 계층(레벨,들여쓰기)

  private int reOrder;

  private String name;

  private Member member;

  private String content;

  private String hostIp;

  private LocalDateTime wDate;

  private String complaint;

  private int replySw; // 원본 댓글 여부

  // 자식 댓글(대댓글) 리스트 - 계층형 구조 지원
  private List<BoardReplyDTO> children;


}
