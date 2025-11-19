package com.example.SpringGroupBB.entity;

import com.example.SpringGroupBB.dto.ThresholdDTO;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "threshold")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThresholdEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String deviceCode;
  private String sensorKey;
  private int alarm;
  private int warning;
  private int status;

  @CreatedDate
  private LocalDateTime updateDateTime;

  // DTO To Entity
  public static ThresholdEntity dtoToEntity(ThresholdDTO dto) {
    return ThresholdEntity.builder()
            .deviceCode(dto.getDeviceCode())
            .sensorKey(dto.getSensorKey())
            .alarm(dto.getAlarm())
            .warning(dto.getWarning())
            .status(dto.getStatus())
            .updateDateTime(LocalDateTime.now())
            .build();
  }

}
