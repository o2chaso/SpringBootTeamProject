package com.example.SpringGroupBB.repository;

import com.example.SpringGroupBB.entity.Board;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long>, QuerydslPredicateExecutor<Board> {

  Page<Board> findByTitleContaining(String searchString, PageRequest pageable);

  Page<Board> findByNameContaining(String searchString, PageRequest pageable);

  Page<Board> findByContentContaining(String searchString, PageRequest pageable);

  @Transactional
  @Modifying
  @Query("update Board b set b.readNum = b.readNum + 1 where b.id = :id")
  void setBoardReadNumPlus(Long id);

  @Query("select b from Board b where b.id < :id order by b.id desc limit 1")
  Board findPrevious(Long id);

  @Query("select b from Board b where b.id > :id order by b.id asc limit 1")
  Board findNext(Long id);

  @Transactional
  @Modifying
  @Query("update Board b set b.good = b.good + 1 where b.id = :id")
  void setBoardGoodNumPlus(Long id);

  @Transactional
  @Modifying
  @Query("update Board b set b.good = b.good + :goodCnt where b.id = :id")
  void setBoardGoodNumPlusMinus(@Param("id") Long id, @Param("goodCnt") int goodCnt);

  @Query("SELECT b FROM Board b ORDER BY CASE WHEN b.noticeSw='OK' THEN 1 ELSE 0 END DESC, b.id DESC")
  List<Board> findAllWithNoticeFirst();

  @Query(value = "SELECT * FROM board WHERE complaint = 'NO' AND open_sw = 'OK' AND (title LIKE CONCAT('%',:searchStr,'%') OR content LIKE CONCAT('%',:searchStr,'%'))", nativeQuery = true)
  List<Board> selectAllSearchStr(@Param("searchStr") String searchStr);

  int countByMemberEmail(String email);
}
