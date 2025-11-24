package com.example.SpringGroupBB.service;

import com.example.SpringGroupBB.dto.SensorDTO;
import com.example.SpringGroupBB.dto.ThresholdDTO;
import com.example.SpringGroupBB.entity.SensorEntity;
import com.example.SpringGroupBB.entity.ThresholdEntity;
import com.example.SpringGroupBB.repository.SensorRepository;
import com.example.SpringGroupBB.repository.ThresholdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

  // 최근 데이터 20건 가져오기
  public List<SensorDTO> getPastSensorData(String deviceCode, String sensorKey, String nowTime) {
    LocalDateTime now = LocalDateTime.parse(nowTime);

    List<SensorEntity> list = sensorRepository
            .findTop20ByDeviceCodeAndMeasureDatetimeLessThanOrderByMeasureDatetimeDesc(deviceCode, now);
    Collections.reverse(list);

    List<SensorDTO> result = new ArrayList<>();

    for(SensorEntity entity : list) {
      SensorDTO dto = SensorDTO.EntityToDTO(entity);
      ThresholdDTO th = null;

      Optional<ThresholdEntity> optional = thresholdRepository.findByDeviceCodeAndSensorKey(deviceCode, sensorKey);
      if(optional.isPresent()) {
        th = ThresholdDTO.entityToDto(optional.get());
      }
      dto.setThreshold(th); // 그래프용으로 임계값 포함
      result.add(dto);
    }
    return result;
  }

}
