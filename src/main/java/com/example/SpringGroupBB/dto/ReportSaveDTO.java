package com.example.SpringGroupBB.dto;

import com.example.SpringGroupBB.entity.ReportSave;
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
public class ReportSaveDTO {
  private Long id;
  private LocalDateTime saveReportDate;
  private LocalDateTime saveReportRestDate;
  private String deviceCode;
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
  private String eventValue;
  private String event;
  private String eventMeasureDatetime;
  private String alarm;
  private String warning;

  public static ReportSaveDTO entityToDTO(ReportSave entity) {
    return ReportSaveDTO.builder()
            .id(entity.getId())
            .saveReportDate(entity.getSaveReportDate())
            .saveReportRestDate(entity.getSaveReportRestDate())
            .deviceCode(entity.getDeviceCode())
            .report(entity.getReport())
            .value1MinData(entity.getValue1MinData())
            .value1AvgData(entity.getValue1AvgData())
            .value1MaxData(entity.getValue1MaxData())
            .value1EventData(entity.getValue1EventData())
            .value1MinRate(entity.getValue1MinRate())
            .value1AvgRate(entity.getValue1AvgRate())
            .value1MaxRate(entity.getValue1MaxRate())
            .value1EventRate(entity.getValue1EventRate())
            .value2MinData(entity.getValue2MinData())
            .value2AvgData(entity.getValue2AvgData())
            .value2MaxData(entity.getValue2MaxData())
            .value2EventData(entity.getValue2EventData())
            .value2MinRate(entity.getValue2MinRate())
            .value2AvgRate(entity.getValue2AvgRate())
            .value2MaxRate(entity.getValue2MaxRate())
            .value2EventRate(entity.getValue2EventRate())
            .value3MinData(entity.getValue3MinData())
            .value3AvgData(entity.getValue3AvgData())
            .value3MaxData(entity.getValue3MaxData())
            .value3EventData(entity.getValue3EventData())
            .value3MinRate(entity.getValue3MinRate())
            .value3AvgRate(entity.getValue3AvgRate())
            .value3MaxRate(entity.getValue3MaxRate())
            .value3EventRate(entity.getValue3EventRate())
            .value4MinData(entity.getValue4MinData())
            .value4AvgData(entity.getValue4AvgData())
            .value4MaxData(entity.getValue4MaxData())
            .value4EventData(entity.getValue4EventData())
            .value4MinRate(entity.getValue4MinRate())
            .value4AvgRate(entity.getValue4AvgRate())
            .value4MaxRate(entity.getValue4MaxRate())
            .value4EventRate(entity.getValue4EventRate())
            .value5MinData(entity.getValue5MinData())
            .value5AvgData(entity.getValue5AvgData())
            .value5MaxData(entity.getValue5MaxData())
            .value5EventData(entity.getValue5EventData())
            .value5MinRate(entity.getValue5MinRate())
            .value5AvgRate(entity.getValue5AvgRate())
            .value5MaxRate(entity.getValue5MaxRate())
            .value5EventRate(entity.getValue5EventRate())
            .value6MinData(entity.getValue6MinData())
            .value6AvgData(entity.getValue6AvgData())
            .value6MaxData(entity.getValue6MaxData())
            .value6EventData(entity.getValue6EventData())
            .value6MinRate(entity.getValue6MinRate())
            .value6AvgRate(entity.getValue6AvgRate())
            .value6MaxRate(entity.getValue6MaxRate())
            .value6EventRate(entity.getValue6EventRate())
            .value7MinData(entity.getValue7MinData())
            .value7AvgData(entity.getValue7AvgData())
            .value7MaxData(entity.getValue7MaxData())
            .value7EventData(entity.getValue7EventData())
            .value7MinRate(entity.getValue7MinRate())
            .value7AvgRate(entity.getValue7AvgRate())
            .value7MaxRate(entity.getValue7MaxRate())
            .value7EventRate(entity.getValue7EventRate())
            .value8MinData(entity.getValue8MinData())
            .value8AvgData(entity.getValue8AvgData())
            .value8MaxData(entity.getValue8MaxData())
            .value8EventData(entity.getValue8EventData())
            .value8MinRate(entity.getValue8MinRate())
            .value8AvgRate(entity.getValue8AvgRate())
            .value8MaxRate(entity.getValue8MaxRate())
            .value8EventRate(entity.getValue8EventRate())
            .value9MinData(entity.getValue9MinData())
            .value9AvgData(entity.getValue9AvgData())
            .value9MaxData(entity.getValue9MaxData())
            .value9EventData(entity.getValue9EventData())
            .value9MinRate(entity.getValue9MinRate())
            .value9AvgRate(entity.getValue9AvgRate())
            .value9MaxRate(entity.getValue9MaxRate())
            .value9EventRate(entity.getValue9EventRate())
            .value10MinData(entity.getValue10MinData())
            .value10AvgData(entity.getValue10AvgData())
            .value10MaxData(entity.getValue10MaxData())
            .value10EventData(entity.getValue10EventData())
            .value10MinRate(entity.getValue10MinRate())
            .value10AvgRate(entity.getValue10AvgRate())
            .value10MaxRate(entity.getValue10MaxRate())
            .value10EventRate(entity.getValue10EventRate())
            .eventValue(entity.getEventValue())
            .event(entity.getEvent())
            .eventMeasureDatetime(entity.getEventMeasureDatetime())
            .alarm(entity.getAlarm())
            .warning(entity.getWarning())
            .build();
  }
  public static List<ReportSaveDTO> entityListToDTOList(List<ReportSave> entityList) {
    List<ReportSaveDTO> dtoList = new ArrayList<>();
    for(ReportSave entity : entityList) {
      dtoList.add(entityToDTO(entity));
    }
    return dtoList;
  }
}
