package com.example.SpringGroupBB.service;

import com.example.SpringGroupBB.entity.SensorEntity;
import com.example.SpringGroupBB.repository.SensorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SensorServiceTest {
  @Autowired
  SensorRepository sensorRepository;

  @Test
  public void sensorDateUpdateTest() {
    List<SensorEntity> sensorList = sensorRepository.findByContainingMeasureDateTime("2025-02-01%");
    System.out.println("sensorList.size: "+sensorList.size());
  }
}