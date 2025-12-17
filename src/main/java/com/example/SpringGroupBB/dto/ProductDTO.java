package com.example.SpringGroupBB.dto;

import com.example.SpringGroupBB.entity.Product;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
  @Length(min = 1, max = 30, message = "센서 타입은 2~30자 입니다.")
  private String sensorType;

  @Length(min = 1, max = 100, message = "한줄소개는 2~100자 이내입니다.")
  private String shortDescription;

  @Length(min = 1, max = 100, message = "특징 소개는 2~100자 이내입니다.")
  private String features;

  @Length(min = 1, max = 30, message = "제조사 소개는 2~30자 이내입니다.")
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
  public static List<ProductDTO> entityListToDTOList(List<Product> productList) {
    List<ProductDTO> productDTOList = new ArrayList<>();
    for(Product product : productList) {
      productDTOList.add(entityToDto(product));
    }
    return productDTOList;
  }
}