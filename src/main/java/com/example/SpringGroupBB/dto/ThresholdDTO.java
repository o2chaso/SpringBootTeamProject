package com.example.SpringGroupBB.dto;

import com.example.SpringGroupBB.entity.ThresholdEntity;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThresholdDTO {
  private Long id;

  private String deviceCode;
  private String sensorKey;
  private int alarm;
  private int warning;
  private int status;

  private LocalDateTime updateDateTime;

  // Entity To DTO
  public static ThresholdDTO entityToDto(ThresholdEntity entity) {
    return ThresholdDTO.builder()
            .id(entity.getId())
            .deviceCode(entity.getDeviceCode())
            .sensorKey(entity.getSensorKey())
            .alarm(entity.getAlarm())
            .warning(entity.getWarning())
            .status(entity.getStatus())
            .updateDateTime(LocalDateTime.now())
            .build();
  }
}
