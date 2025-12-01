package com.example.SpringGroupBB.dto;

import com.example.SpringGroupBB.entity.Product;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {

  private Long id;

  @NotEmpty(message = "센서 이름은 필수 입력입니다.")
  @Length(min = 1, max = 20, message = "센서 이름은 2~20자 입니다.")
  private String sensorName;

  @NotEmpty(message = "모델 이름은 필수 입력입니다.")
  @Length(min = 1, max = 30, message = "모델 이름은 2~30자 입니다.")
  private String model;

  @NotEmpty(message = "센서 타입은 필수 입력입니다.")
  @Length(min = 1, max = 20, message = "센서 이름은 2~20자 입니다.")
  private String sensorType;

  private String shortDescription;

  private String features;

  private String manufacturer;

  private String sensorImage;

  private LocalDateTime wDate;

  private MultipartFile sFile;

  public static ProductDTO entityToDto(Product product) {
    return ProductDTO.builder()
            .id(product.getId())
            .sensorName(product.getSensorName())
            .model(product.getModel())
            .sensorType(product.getSensorType())
            .shortDescription(product.getShortDescription())
            .features(product.getFeatures())
            .manufacturer(product.getManufacturer())
            .sensorImage(product.getSensorImage())
            .wDate(product.getWDate())
            .build();
  }
}