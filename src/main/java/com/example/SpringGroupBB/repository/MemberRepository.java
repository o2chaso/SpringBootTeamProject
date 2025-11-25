package com.example.SpringGroupBB.repository;

import com.example.SpringGroupBB.entity.Member;
import org.hibernate.type.descriptor.converter.spi.JpaAttributeConverter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;


public interface MemberRepository extends JpaRepository<Member, Long> {

  Optional<Member> findByEmail(String email);

  @Query("select case when count(m) > 0 then true else false end from Member m where m.email = :email")
    boolean existsByEmail(String email);

  Page<Member> findByEmailContaining(String searchString, PageRequest pageable);

  Page<Member> findByNameContaining(String searchString, PageRequest pageable);
}
