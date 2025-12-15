package com.example.SpringGroupBB.dto;

import com.example.SpringGroupBB.entity.EventLog;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventLogDTO {

  private String deviceCode;
  private String sensorKey;
  private Double value;
  private String event; // 정상, Alarm, Warning

  @CreatedDate
  private LocalDateTime measureDatetime;

  public EventLogDTO(String deviceCode, String sensorKey, double value, String event, LocalDateTime measureDatetime) {
    this.deviceCode = deviceCode;
    this.sensorKey = sensorKey;
    this.value = value;
    this.event = event;
    this.measureDatetime = measureDatetime;
  }

  // Entity To DTO
  public static EventLogDTO entityToDto(EventLog entity) {
    return EventLogDTO.builder()
            .deviceCode(entity.getDeviceCode())
            .sensorKey(entity.getSensorKey())
            .value(entity.getValue())
            .event(entity.getEvent())
            .measureDatetime(entity.getMeasureDatetime())
            .build();
  }


}
