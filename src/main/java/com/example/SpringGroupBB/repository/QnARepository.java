package com.example.SpringGroupBB.repository;

import com.example.SpringGroupBB.constant.Progress;
import com.example.SpringGroupBB.entity.Member;
import com.example.SpringGroupBB.entity.QnA;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QnARepository extends JpaRepository<QnA, Long> {
  Page<QnA> findByFromEmailAndProgressNotOrderByOpenSWDescLastDateDesc(Member fromEmail, Progress progress, PageRequest pageable);

  List<QnA> findByParentId(Long id);

  List<QnA> findAllByProgressNotOrderByLastDateDesc(Progress progress);

  Page<QnA> findByProgressOrderByOpenSWAscLastDateDesc(Progress progress, PageRequest pageable);

  Page<QnA> findByFromEmailAndProgressOrderByOpenSWDescLastDateDesc(Member fromEmail, Progress progress, PageRequest pageable);

  @Transactional
  @Modifying(clearAutomatically = true)
  void deleteByParentId(Long id);

  Page<QnA> findByProgressOrProgressOrderByOpenSWAscLastDateDesc(Progress progress1, Progress progress2, PageRequest pageable);

  @Query(value = "SELECT * FROM qna WHERE from_email = :email AND (title LIKE CONCAT('%', :searchStr, '%') OR content LIKE CONCAT('%', :searchStr, '%'))", nativeQuery = true)
  List<QnA> selectSearch(@Param("email")String email, @Param("searchStr")String searchStr);

  @Query(value = "SELECT * FROM qna WHERE progress != 'ANSWER' ORDER BY CASE progress WHEN 'RESOLVING' THEN 1 WHEN 'RESOLVED' THEN 2 WHEN 'UNRESOLVABLE' THEN 3 END ASC, opensw, last_date DESC LIMIT :startIndexNo, :pageSize", nativeQuery = true)
  List<QnA> selectQnAAdminProgress(int startIndexNo, int pageSize);

  @Query(value = "SELECT count(qna_id) FROM qna WHERE progress != 'ANSWER'", nativeQuery = true)
  int selectQnATotRecCnt();
}
