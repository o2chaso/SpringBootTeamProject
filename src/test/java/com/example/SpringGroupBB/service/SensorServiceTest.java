package com.example.SpringGroupBB.service;

import com.example.SpringGroupBB.entity.SensorEntity;
import com.example.SpringGroupBB.repository.SensorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
class SensorServiceTest {
  @Autowired
  SensorRepository sensorRepository;

  @Test
  public void sensorDateUpdateTest() {
    List<SensorEntity> sensorList = sensorRepository.selectMeasureDatetime("2025-02-17");
    System.out.println("sensorList.size: "+sensorList.size());
    sensorList.forEach(entity -> {
      entity.setMeasureDatetime(LocalDateTime.parse("2025-12-03"+entity.getMeasureDatetime().toString().substring(10)));
      sensorRepository.save(entity);
    });

    System.out.println("날짜 업데이트 완료.");
  }
}