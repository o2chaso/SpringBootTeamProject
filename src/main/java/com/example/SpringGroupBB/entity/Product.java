package com.example.SpringGroupBB.entity;

import com.example.SpringGroupBB.dto.ProductDTO;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "product_id")
  private Long id;

  @Column(length = 30, nullable = false)
  private String model;

  @Column(length = 20, nullable = false)
  private String sensorName;

  @Column(length = 30, nullable = false)
  private String sensorType;

  @Column(length = 100)
  private String shortDescription;

  @Column(length = 100)
  private String features;

  @Column(length = 30)
  private String manufacturer;

  @Column(nullable = false)
  private String sensorImage;

  @CreatedDate
  private LocalDateTime wDate;

  public static Product dtoToEntity(ProductDTO dto) {
    return Product.builder()
            .sensorName(dto.getSensorName())
            .model(dto.getModel())
            .sensorType(dto.getSensorType())
            .shortDescription(dto.getShortDescription())
            .features(dto.getFeatures())
            .manufacturer(dto.getManufacturer())
            .sensorImage(dto.getSensorImage())
            .wDate(dto.getWDate())
            .build();
  }

}
