package com.example.SpringGroupBB.repository;


import com.example.SpringGroupBB.entity.MemberEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
  Optional<MemberEntity> findByEmail(String email);

  @Transactional
  void deleteByEmail(String email);

  boolean existsByEmail(String email);
}
