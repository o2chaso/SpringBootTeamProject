package com.example.SpringGroupBB.service;

import com.example.SpringGroupBB.dto.ThresholdDTO;
import com.example.SpringGroupBB.entity.ThresholdEntity;
import com.example.SpringGroupBB.repository.ThresholdRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ThresholdService {
  private final ThresholdRepository thresholdRepository;

  public List<ThresholdDTO> getThresholds() {
    List<ThresholdEntity> entityList = thresholdRepository.findAll();
    return entityList.stream().map(ThresholdDTO::entityToDto).toList();
  }

  // Interlock Save
  @Transactional
  public void saveThreshold(ThresholdDTO dto) {

    // Interlock data 확인
    Optional<ThresholdEntity> optional = thresholdRepository.findByDeviceCodeAndSensorKey(dto.getDeviceCode(), dto.getSensorKey());
    if(optional.isPresent()) {
      // 있으면 덮어쓰기
      ThresholdEntity existing = optional.get();
      existing.setAlarm(dto.getAlarm());
      existing.setWarning(dto.getWarning());
      existing.setStatus(dto.getStatus());
      existing.setUpdateDateTime(LocalDateTime.now());

      thresholdRepository.save(existing);   // update
    }
    // 없으면 새로 저장
    else {
      ThresholdEntity saveInterlock = ThresholdEntity.builder()
              .deviceCode(dto.getDeviceCode())
              .sensorKey(dto.getSensorKey())
              .alarm(dto.getAlarm())
              .warning(dto.getWarning())
              .status(dto.getStatus())
              .updateDateTime(LocalDateTime.now())
              .build();
      thresholdRepository.save(saveInterlock);  // insert
    }
  }

  // 특정 장비(deviceCode) + 특정 센서(sensorKey)의 인터락(Threshold) 1개를 가져온다.
  public Optional<ThresholdEntity> findThreshold(String deviceCode, String sensorKey) {
    return thresholdRepository.findByDeviceCodeAndSensorKey(deviceCode, sensorKey);
  }
}
