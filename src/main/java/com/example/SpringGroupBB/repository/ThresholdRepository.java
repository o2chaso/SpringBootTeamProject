package com.example.SpringGroupBB.repository;

import com.example.SpringGroupBB.entity.ThresholdEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ThresholdRepository extends JpaRepository<ThresholdEntity, Long> {
  Optional<ThresholdEntity> findByDeviceCodeAndSensorKey(String deviceCode, String sensorKey);
}
