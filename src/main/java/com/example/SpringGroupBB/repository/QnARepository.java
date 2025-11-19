package com.example.SpringGroupBB.repository;

import com.example.SpringGroupBB.constant.Progress;
import com.example.SpringGroupBB.entity.MemberEntity;
import com.example.SpringGroupBB.entity.MemberEntity;
import com.example.SpringGroupBB.entity.QnA;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QnARepository extends JpaRepository<QnA, Long> {
  Page<QnA> findByFromEmailAndProgressNot(MemberEntity fromEmail, Progress progress, PageRequest pageable);

  List<QnA> findByParentId(Long id);

  List<QnA> findAllByProgressNotOrderByLastDateDesc(Progress progress);

  Page<QnA> findByProgress(Progress progress, PageRequest pageable);

  Page<QnA> findAllByProgressNot(Progress progress, PageRequest pageable);

  Page<QnA> findByFromEmailAndProgress(MemberEntity fromEmail, Progress progress, PageRequest pageable);
}
