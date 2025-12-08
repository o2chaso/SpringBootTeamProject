package com.example.SpringGroupBB.repository;

import com.example.SpringGroupBB.dto.LoginHistoryDTO;
import com.example.SpringGroupBB.entity.LoginHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long> {

  Page<LoginHistory> findByMember_NameContaining(String searchString, PageRequest pageable);

  Page<LoginHistory> findByMember_EmailContaining(String searchString, PageRequest pageable);

  Page<LoginHistory> findByMember_NameContainingAndCreateDateBetween(
          String searchString, LocalDateTime start, LocalDateTime end, PageRequest pageable);

  Page<LoginHistory> findByMember_EmailContainingAndCreateDateBetween(
          String searchString, LocalDateTime start, LocalDateTime end, PageRequest pageable);

  Page<LoginHistory> findByCreateDateBetween(
          LocalDateTime start, LocalDateTime end, PageRequest pageable);

  List<LoginHistory> findByCreateDateBetween(LocalDateTime start, LocalDateTime end);

  List<LoginHistory> findByMember_IdAndCreateDateBetween(Long id, LocalDateTime startDateTime, LocalDateTime endDateTime);
}
