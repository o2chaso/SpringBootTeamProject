package com.example.SpringGroupBB.repository;

import com.example.SpringGroupBB.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
  Optional<Member> findByMid(String name);
}
