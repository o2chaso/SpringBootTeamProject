package com.example.SpringGroupBB.entity;

import com.example.SpringGroupBB.dto.EventLogDTO;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
public class EventLog {

  @Id
  @Column(name = "event_log_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String deviceCode;
  private String sensorKey;
  private Double value;
  private String event; // 정상, Alarm, Warning

  @CreatedDate
  private LocalDateTime measureDatetime;

  // DTO To Entity
  public static EventLog dtoToEntity(EventLogDTO dto) {
    return EventLog.builder()
            .deviceCode(dto.getDeviceCode())
            .sensorKey(dto.getSensorKey())
            .value(dto.getValue())
            .event(dto.getEvent())
            .measureDatetime(dto.getMeasureDatetime())
            .build();
  }

}
