package com.example.SpringGroupBB.repository;

import com.example.SpringGroupBB.entity.SensorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface SensorRepository extends JpaRepository<SensorEntity, Long> {

  // 특정 deviceCode에 대해 현재 시간(now)보다 과거(<= now) 데이터 중 가장 최근(MeasureDatetime DESC) 1개를 조회한다.
  SensorEntity findTopByDeviceCodeAndMeasureDatetimeLessThanEqualOrderByMeasureDatetimeDesc(String deviceCode, LocalDateTime now);

  @Query("SELECT DISTINCT s.deviceCode FROM SensorEntity s")
  List<String> findAllDeviceCodes();
}
