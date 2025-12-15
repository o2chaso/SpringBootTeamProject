package com.example.SpringGroupBB.dto;

import com.example.SpringGroupBB.entity.SensorEntity;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

  // 임계값(인터락)
  private ThresholdDTO threshold;

  private LocalDateTime measureDatetime;

  // 일일 리포트 시작
  private double minData;
  private double avgData;
  private double maxData;
  private int eventData;

  private Double minRate;
  private Double avgRate;
  private Double maxRate;
  private int eventRate;

  // 이벤트.
  private String eventValue;
  private String event;
  private String eventMeasureDatetime;
  private int alarm;
  private int warning;

  public SensorDTO(double minData, double avgData, double maxData, int eventData,
                   int alarm, int warning) {
    this.minData = minData;
    this.avgData = avgData;
    this.maxData = maxData;
    this.eventData = eventData;
    this.alarm = alarm;
    this.warning = warning;
  }
  // 일일 리포트 끝

  // Entity To DTO
  public static SensorDTO entityToDTO(SensorEntity entity) {
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
            .minData(entity.getMinData())
            .avgData(entity.getAvgData())
            .maxData(entity.getMaxData())
            .build();
  }
  // EntityList To DTOList
  public static List<SensorDTO> entityListToDTOList(List<SensorEntity> entityList) {
    List<SensorDTO> dtoList = new ArrayList<>();
    for(SensorEntity entity : entityList) {
      dtoList.add(entityToDTO(entity));
    }
    return dtoList;
  }

}
