package com.example.SpringGroupBB.repository;

import com.example.SpringGroupBB.entity.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository <CompanyEntity, Long>{
}
