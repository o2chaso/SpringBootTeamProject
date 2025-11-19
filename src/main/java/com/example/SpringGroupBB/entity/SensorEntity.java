package com.example.SpringGroupBB.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "sensor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SensorEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "sensor_id")
  private Long Id;

  private Long companyId;

  private String deviceCode;

  @CreatedDate
  private LocalDateTime createdDate;

  @CreatedDate
  private LocalDateTime updatedDate;



}
