package com.example.SpringGroupBB.repository;

import com.example.SpringGroupBB.entity.Product;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

  Page<Product> findBySensorNameContaining(String searchString, PageRequest pageable);

  Page<Product> findByManufacturerContaining(String searchString, PageRequest pageable);

  Optional<Product> findByModel(String model);
}
