package com.example.SpringGroupBB.repository;

import com.example.SpringGroupBB.dto.SensorDTO;
import com.example.SpringGroupBB.entity.SensorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SensorRepository extends JpaRepository<SensorEntity, Long> {

  // 특정 deviceCode에 대해 현재 시간(now)보다 과거(<= now) 데이터 중 가장 최근(MeasureDatetime DESC) 1개를 조회한다.
  SensorEntity findTopByDeviceCodeAndMeasureDatetimeLessThanEqualOrderByMeasureDatetimeDesc(String deviceCode, LocalDateTime now);

  @Query("SELECT DISTINCT s.deviceCode FROM SensorEntity s")
  List<String> findAllDeviceCodes();

  // 일일 리포트 검색용.
  @Query(value = "SELECT MIN(value_1), AVG(value_1), MAX(value_1) FROM sensor WHERE measure_datetime LIKE CONCAT(:day,'%')", nativeQuery = true)
  SensorDTO selectDailyReport(String day);

  // 날짜 데이터 업데이트용.
  @Query(value = "SELECT * FROM sensor WHERE measure_datetime LIKE CONCAT(:day,'%') ORDER BY measure_datetime", nativeQuery = true)
  List<SensorEntity> selectDailyReportUpdate(String day);
}
