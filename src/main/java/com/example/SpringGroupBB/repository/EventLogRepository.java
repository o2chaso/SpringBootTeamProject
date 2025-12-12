package com.example.SpringGroupBB.repository;

import com.example.SpringGroupBB.entity.EventLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventLogRepository extends JpaRepository<EventLog, Long> {
  List<EventLog> findTop20ByDeviceCodeAndSensorKeyOrderByMeasureDatetimeDesc(String deviceCode, String sensorKey);

  Page<EventLog> findByDeviceCodeAndSensorKey(String deviceCode, String sensorKey, PageRequest pageable);
}
