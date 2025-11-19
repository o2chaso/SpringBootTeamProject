package com.example.SpringGroupBB.dto;

import com.example.SpringGroupBB.constant.EventType;
import com.example.SpringGroupBB.entity.EventLogEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
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

  private Long Id;
  private Long sensorId;
  private Long companyId;

  private String deviceCode;

  private EventType eventType;

  // 이벤트 발생 순간의 실제 센서 측정값
  private Double eventValue;

  // 해당 센서에 대해 관리자가 설정한 임계 기준값
  private Double thresholdValue;

  // 이벤트 발생 직전 값
  private Double beforeValue;

  // 이벤트 발생 직후 값
  private Double afterValue;

  private LocalDateTime eventDateTime;

  // Entity To DTO
  public static EventLogDTO entityToDto(EventLogEntity entity) {
    return EventLogDTO.builder()
            .Id(entity.getId())
            .sensorId(entity.getSensorId())
            .companyId(entity.getCompanyId())
            .deviceCode(entity.getDeviceCode())
            .eventType(entity.getEventType())
            .eventValue(entity.getEventValue())
            .thresholdValue(entity.getThresholdValue())
            .beforeValue(entity.getBeforeValue())
            .afterValue(entity.getAfterValue())
            .eventDateTime(entity.getEventDateTime())
            .build();
  }
}
