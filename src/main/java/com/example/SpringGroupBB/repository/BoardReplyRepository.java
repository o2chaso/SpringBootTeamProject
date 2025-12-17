package com.example.SpringGroupBB.repository;

import com.example.SpringGroupBB.entity.BoardReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;
import java.util.Optional;

public interface BoardReplyRepository extends JpaRepository<BoardReply, Long>, QuerydslPredicateExecutor<BoardReply> {

  // 해당 게시글의 모든 댓글 조회
  List<BoardReply> findByBoardIdOrderById(Long boardId);

  // 댓글 1개만 Optional로 조회
  Optional<BoardReply> findByBoardId(Long id);

  // 특정 회원이 작성한 댓글 갯수 조회
  int countByMemberEmail(String email);

  void deleteByBoardId(Long id);
}
