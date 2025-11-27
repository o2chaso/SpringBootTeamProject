package com.example.SpringGroupBB.service;

import com.example.SpringGroupBB.dto.EventLogDTO;
import com.example.SpringGroupBB.dto.ThresholdDTO;
import com.example.SpringGroupBB.entity.EventLog;
import com.example.SpringGroupBB.repository.EventLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventLogService {

  private final EventLogRepository eventLogRepository;

  /* 어려워서 메모하면서 만든 서비스니까 주석은 지워도 좋습니다.
    EventLog 저장 서비스 (상태 변화 시 저장)
    @Param deviceCode(해당 장치), sensorKey(value1~13), value(센서 값), event(상태: 정상, 알람, 워닝)
  */

  public void saveEventLog(String deviceCode, String sensorKey, Double value, String event) {
    EventLogDTO dto = EventLogDTO.builder()
            .deviceCode(deviceCode)
            .sensorKey(sensorKey)
            .value(value)
            .event(event)
            .build();
    eventLogRepository.save(EventLog.dtoToEntity(dto));
  }

  // 현재 센서 상태 판단하기
  public String checkSensorState(Double value, ThresholdDTO th) {
    if(th == null) return "Normal";     // 정상 값
    if(value == null) return "Normal";  // 값 자체가 없으면 정상

    if(value >= th.getAlarm() || value < -50) return "Alarm";
    if(value >= th.getWarning()) return "Warning";
    return "Normal";
  }

}
