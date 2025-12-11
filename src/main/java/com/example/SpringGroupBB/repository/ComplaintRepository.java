package com.example.SpringGroupBB.repository;

import com.example.SpringGroupBB.entity.Complaint;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

  Page<Complaint>findByProgress(String progress, Pageable pageable);
  Page<Complaint>findAll(Pageable pageable);
  Optional<Complaint> findTop1ByPartIdOrderByIdDesc(long partId);

  @Modifying
  @Transactional
  @Query("update Complaint c set c.progress = :progress where c.id = :id")
  int updateComplaintStatus(@Param("id") Long id,
                            @Param("progress") String progress);

  int deleteByPartIdAndPart(int partId, String part);
}


