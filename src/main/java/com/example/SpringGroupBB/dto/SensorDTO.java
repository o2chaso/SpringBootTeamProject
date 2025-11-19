package com.example.SpringGroupBB.dto;

import com.example.SpringGroupBB.entity.SensorEntity;
import jakarta.persistence.Column;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SensorDTO {

  private Long Id;
  private Long companyId;
  private String deviceCode;

  private Double value1;
  private Double value2;
  private Double value3;
  private Double value4;
  private Double value5;
  private Double value6;
  private Double value7;
  private Double value8;
  private Double value9;
  private Double value10;
  private Double value11;
  private Double value12;
  private Double value13;


  private LocalDateTime measureDatetime;

  // Entity To DTO
  public static SensorDTO EntityToDTO(SensorEntity entity) {
    return SensorDTO.builder()
            .Id(entity.getId())
            .companyId(entity.getCompanyId())
            .deviceCode(entity.getDeviceCode())
            .value1(entity.getValue1())
            .value2(entity.getValue2())
            .value3(entity.getValue3())
            .value4(entity.getValue4())
            .value5(entity.getValue5())
            .value6(entity.getValue6())
            .value7(entity.getValue7())
            .value8(entity.getValue8())
            .value9(entity.getValue9())
            .value10(entity.getValue10())
            .value11(entity.getValue11())
            .value12(entity.getValue12())
            .value13(entity.getValue13())
            .measureDatetime(entity.getMeasureDatetime())
            .build();
  }
}
