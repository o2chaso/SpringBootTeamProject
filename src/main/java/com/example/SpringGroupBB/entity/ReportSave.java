package com.example.SpringGroupBB.entity;

import com.example.SpringGroupBB.dto.ReportSaveDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportSave {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "reportID")
  private Long id;
  @Column(nullable = false)
  private LocalDateTime saveReportDate;
  @Column(nullable = false)
  private LocalDateTime saveReportRestDate;
  @Column(nullable = false)
  private String deviceCode;
  @Column(nullable = false)
  private String report;
  // 실내온도.
  private Double value1MinData;
  private Double value1AvgData;
  private Double value1MaxData;
  private int value1EventData;
  private Double value1MinRate;
  private Double value1AvgRate;
  private Double value1MaxRate;
  private int value1EventRate;
  // 상대습도.
  private Double value2MinData;
  private Double value2AvgData;
  private Double value2MaxData;
  private int value2EventData;
  private Double value2MinRate;
  private Double value2AvgRate;
  private Double value2MaxRate;
  private int value2EventRate;
  // 이산화탄소.
  private Double value3MinData;
  private Double value3AvgData;
  private Double value3MaxData;
  private int value3EventData;
  private Double value3MinRate;
  private Double value3AvgRate;
  private Double value3MaxRate;
  private int value3EventRate;
  // 유기화합물VOC.
  private Double value4MinData;
  private Double value4AvgData;
  private Double value4MaxData;
  private int value4EventData;
  private Double value4MinRate;
  private Double value4AvgRate;
  private Double value4MaxRate;
  private int value4EventRate;
  // 미세먼지.
  private Double value5MinData;
  private Double value5AvgData;
  private Double value5MaxData;
  private int value5EventData;
  private Double value5MinRate;
  private Double value5AvgRate;
  private Double value5MaxRate;
  private int value5EventRate;
  // 초미세먼지.
  private Double value6MinData;
  private Double value6AvgData;
  private Double value6MaxData;
  private int value6EventData;
  private Double value6MinRate;
  private Double value6AvgRate;
  private Double value6MaxRate;
  private int value6EventRate;
  // 온도_1.
  private Double value7MinData;
  private Double value7AvgData;
  private Double value7MaxData;
  private int value7EventData;
  private Double value7MinRate;
  private Double value7AvgRate;
  private Double value7MaxRate;
  private int value7EventRate;
  // 온도_2.
  private Double value8MinData;
  private Double value8AvgData;
  private Double value8MaxData;
  private int value8EventData;
  private Double value8MinRate;
  private Double value8AvgRate;
  private Double value8MaxRate;
  private int value8EventRate;
  // 온도_3.
  private Double value9MinData;
  private Double value9AvgData;
  private Double value9MaxData;
  private int value9EventData;
  private Double value9MinRate;
  private Double value9AvgRate;
  private Double value9MaxRate;
  private int value9EventRate;
  // 온도(비접촉).
  private Double value10MinData;
  private Double value10AvgData;
  private Double value10MaxData;
  private int value10EventData;
  private Double value10MinRate;
  private Double value10AvgRate;
  private Double value10MaxRate;
  private int value10EventRate;
  // 이벤트.
  @Lob
  private String eventValue;
  @Lob
  private String event;
  @Lob
  private String eventMeasureDatetime;
  private String alarm;
  private String warning;

  public static ReportSave dtoToEntity(ReportSaveDTO dto) {
    return ReportSave.builder()
            .id(dto.getId())
            .saveReportDate(dto.getSaveReportDate())
            .saveReportRestDate(dto.getSaveReportRestDate())
            .deviceCode(dto.getDeviceCode())
            .report(dto.getReport())
            .value1MinData(dto.getValue1MinData())
            .value1AvgData(dto.getValue1AvgData())
            .value1MaxData(dto.getValue1MaxData())
            .value1EventData(dto.getValue1EventData())
            .value1MinRate(dto.getValue1MinRate())
            .value1AvgRate(dto.getValue1AvgRate())
            .value1MaxRate(dto.getValue1MaxRate())
            .value1EventRate(dto.getValue1EventRate())
            .value2MinData(dto.getValue2MinData())
            .value2AvgData(dto.getValue2AvgData())
            .value2MaxData(dto.getValue2MaxData())
            .value2EventData(dto.getValue2EventData())
            .value2MinRate(dto.getValue2MinRate())
            .value2AvgRate(dto.getValue2AvgRate())
            .value2MaxRate(dto.getValue2MaxRate())
            .value2EventRate(dto.getValue2EventRate())
            .value3MinData(dto.getValue3MinData())
            .value3AvgData(dto.getValue3AvgData())
            .value3MaxData(dto.getValue3MaxData())
            .value3EventData(dto.getValue3EventData())
            .value3MinRate(dto.getValue3MinRate())
            .value3AvgRate(dto.getValue3AvgRate())
            .value3MaxRate(dto.getValue3MaxRate())
            .value3EventRate(dto.getValue3EventRate())
            .value4MinData(dto.getValue4MinData())
            .value4AvgData(dto.getValue4AvgData())
            .value4MaxData(dto.getValue4MaxData())
            .value4EventData(dto.getValue4EventData())
            .value4MinRate(dto.getValue4MinRate())
            .value4AvgRate(dto.getValue4AvgRate())
            .value4MaxRate(dto.getValue4MaxRate())
            .value4EventRate(dto.getValue4EventRate())
            .value5MinData(dto.getValue5MinData())
            .value5AvgData(dto.getValue5AvgData())
            .value5MaxData(dto.getValue5MaxData())
            .value5EventData(dto.getValue5EventData())
            .value5MinRate(dto.getValue5MinRate())
            .value5AvgRate(dto.getValue5AvgRate())
            .value5MaxRate(dto.getValue5MaxRate())
            .value5EventRate(dto.getValue5EventRate())
            .value6MinData(dto.getValue6MinData())
            .value6AvgData(dto.getValue6AvgData())
            .value6MaxData(dto.getValue6MaxData())
            .value6EventData(dto.getValue6EventData())
            .value6MinRate(dto.getValue6MinRate())
            .value6AvgRate(dto.getValue6AvgRate())
            .value6MaxRate(dto.getValue6MaxRate())
            .value6EventRate(dto.getValue6EventRate())
            .value7MinData(dto.getValue7MinData())
            .value7AvgData(dto.getValue7AvgData())
            .value7MaxData(dto.getValue7MaxData())
            .value7EventData(dto.getValue7EventData())
            .value7MinRate(dto.getValue7MinRate())
            .value7AvgRate(dto.getValue7AvgRate())
            .value7MaxRate(dto.getValue7MaxRate())
            .value7EventRate(dto.getValue7EventRate())
            .value8MinData(dto.getValue8MinData())
            .value8AvgData(dto.getValue8AvgData())
            .value8MaxData(dto.getValue8MaxData())
            .value8EventData(dto.getValue8EventData())
            .value8MinRate(dto.getValue8MinRate())
            .value8AvgRate(dto.getValue8AvgRate())
            .value8MaxRate(dto.getValue8MaxRate())
            .value8EventRate(dto.getValue8EventRate())
            .value9MinData(dto.getValue9MinData())
            .value9AvgData(dto.getValue9AvgData())
            .value9MaxData(dto.getValue9MaxData())
            .value9EventData(dto.getValue9EventData())
            .value9MinRate(dto.getValue9MinRate())
            .value9AvgRate(dto.getValue9AvgRate())
            .value9MaxRate(dto.getValue9MaxRate())
            .value9EventRate(dto.getValue9EventRate())
            .value10MinData(dto.getValue10MinData())
            .value10AvgData(dto.getValue10AvgData())
            .value10MaxData(dto.getValue10MaxData())
            .value10EventData(dto.getValue10EventData())
            .value10MinRate(dto.getValue10MinRate())
            .value10AvgRate(dto.getValue10AvgRate())
            .value10MaxRate(dto.getValue10MaxRate())
            .value10EventRate(dto.getValue10EventRate())
            .eventValue(dto.getEventValue())
            .event(dto.getEvent())
            .eventMeasureDatetime(dto.getEventMeasureDatetime())
            .alarm(dto.getAlarm())
            .warning(dto.getWarning())
            .build();
  }
}
