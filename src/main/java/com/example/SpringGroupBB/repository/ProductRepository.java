package com.example.SpringGroupBB.repository;

import com.example.SpringGroupBB.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

  Page<Product> findBySensorNameContaining(String searchString, PageRequest pageable);

  Page<Product> findByManufacturerContaining(String searchString, PageRequest pageable);

  Optional<Product> findByModel(String model);

  @Query(value = "SELECT * FROM product " +
          "WHERE sensor_name LIKE CONCAT('%', :searchStr, '%') OR " +
          "model LIKE CONCAT('%', :searchStr, '%') OR " +
          "sensor_type LIKE CONCAT('%', :searchStr, '%') OR " +
          "short_description LIKE CONCAT('%', :searchStr, '%') OR " +
          "features LIKE CONCAT('%', :searchStr, '%') OR " +
          "manufacturer LIKE CONCAT('%', :searchStr, '%')", nativeQuery = true)
  List<Product> selectSearch(@Param("searchStr") String searchStr);
}
