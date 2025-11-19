package com.example.SpringGroupBB.service;

import com.example.SpringGroupBB.entity.SensorEntity;
import com.example.SpringGroupBB.repository.SensorRepository;
import com.example.SpringGroupBB.repository.ThresholdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SensorService {

  private final SensorRepository sensorRepository;
  private final ThresholdRepository thresholdRepository;


  // 최신 센서값 개 가져오기
  /* public SensorEntity getSensor() {
    LocalDateTime now = LocalDateTime.now();
    return sensorRepository.findTopByMeasureDatetimeLessThanEqualOrderByMeasureDatetimeDesc(deviceCode, now);
  } */

  // device_code 전체에 대한 최신 센서값 리스트
  public List<SensorEntity> getSensorList() {
    LocalDateTime now = LocalDateTime.now();
    List<String> deviceCode = sensorRepository.findAllDeviceCodes();

    List<SensorEntity> list = new ArrayList<>();
    for(String code : deviceCode) {
      SensorEntity sensor = sensorRepository.findTopByDeviceCodeAndMeasureDatetimeLessThanEqualOrderByMeasureDatetimeDesc(code, now);

      if(sensor != null) {
        list.add(sensor);
      }
    }
    return list;
  }


}
