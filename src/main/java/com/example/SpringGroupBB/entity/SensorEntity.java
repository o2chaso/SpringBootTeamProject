package com.example.SpringGroupBB.entity;

import com.example.SpringGroupBB.dto.SensorDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "sensor")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
@Builder
public class SensorEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "sensor_id")
  private Long Id;

  @Column(name = "company_id")
  private Long companyId;
  
  // 장치 구분자
  private String deviceCode;

  // 실내온도
  @Column(name = "value_1")
  private Double value1;
  // 상대습도
  @Column(name = "value_2")
  private Double value2;
  // 이산화탄소
  @Column(name = "value_3")
  private Double value3;
  // 유기화합물VOC
  @Column(name = "value_4")
  private Double value4;
  // 초미세먼지(PM1.0 / PM2.5 / PM10)
  @Column(name = "value_5")
  private Double value5;
  @Column(name = "value_6")
  private Double value6;
  @Column(name = "value_7")
  private Double value7;
  // 온도_1
  @Column(name = "value_8")
  private Double value8;
  // 온도_2
  @Column(name = "value_9")
  private Double value9;
  // 온도_3
  @Column(name = "value_10")
  private Double value10;
  // 온도(비접촉)
  @Column(name = "value_11")
  private Double value11;
  // 소음
  @Column(name = "value_12")
  private Double value12;
  // 조도
  @Column(name = "value_13")
  private Double value13;

  @CreatedDate
  @Column(name = "measure_datetime")
  private LocalDateTime measureDatetime;

  // 일일 리포트 시작
  @Transient
  private double minData;
  @Transient
  private double avgData;
  @Transient
  private double maxData;
  // 일일 리포트 끝

  // 연관관계 설정(한 회사는 여러가지의 센서를 가질 수 있다.)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "company_id", nullable = false, insertable = false, updatable = false)
  @JsonIgnore
  @ToString.Exclude
  private CompanyEntity company;


  // DTO To Entity
  public static SensorEntity dtoToEntity(SensorDTO dto) {
    return SensorEntity.builder()
            .Id(dto.getId())
            .companyId(dto.getCompanyId())
            .deviceCode(dto.getDeviceCode())
            .value1(dto.getValue1())
            .value2(dto.getValue2())
            .value3(dto.getValue3())
            .value4(dto.getValue4())
            .value5(dto.getValue5())
            .value6(dto.getValue6())
            .value7(dto.getValue7())
            .value8(dto.getValue8())
            .value9(dto.getValue9())
            .value10(dto.getValue10())
            .value11(dto.getValue11())
            .value12(dto.getValue12())
            .value13(dto.getValue13())
            .measureDatetime(dto.getMeasureDatetime())
            .build();

  }

}
