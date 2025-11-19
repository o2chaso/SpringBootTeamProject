package com.example.SpringGroupBB.entity;

import com.example.SpringGroupBB.constant.EventType;
import com.example.SpringGroupBB.dto.EventLogDTO;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_log")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventLogEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "event_log_id")
  private Long Id;
  @Column(name = "sensor_id", nullable = false)
  private Long sensorId;
  @Column(name = "company_id", nullable = false)
  private Long companyId;

  private String deviceCode;

  @Column(name = "event_type", nullable = false)
  @Enumerated(EnumType.STRING)
  private EventType eventType;

  // 이벤트 발생 순간의 실제 센서 측정값
  @Column(name = "event_value", nullable = false)
  private Double eventValue;

  // 해당 센서에 대해 관리자가 설정한 임계 기준값
  @Column(name = "threshold_value", nullable = false)
  private Double thresholdValue;

  // 이벤트 발생 직전 값
  @Column(name = "before_value")
  private Double beforeValue;

  // 이벤트 발생 직후 값
  @Column(name = "after_value")
  private Double afterValue;

  @CreatedDate
  @Column(name = "event_datetime", nullable = false)
  private LocalDateTime eventDateTime;

  // DTO To Entity
  public static EventLogEntity dtoToEntity(EventLogDTO dto) {
    return EventLogEntity.builder()
            .Id(dto.getId())
            .sensorId(dto.getSensorId())
            .companyId(dto.getCompanyId())
            .deviceCode(dto.getDeviceCode())
            .eventType(dto.getEventType())
            .eventValue(dto.getEventValue())
            .thresholdValue(dto.getThresholdValue())
            .beforeValue(dto.getBeforeValue())
            .afterValue(dto.getAfterValue())
            .eventDateTime(dto.getEventDateTime())
            .build();
  }
}

