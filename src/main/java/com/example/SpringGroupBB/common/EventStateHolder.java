package com.example.SpringGroupBB.common;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class EventStateHolder {
  // EventLog 저장을 위한 상태저장소 만들기
  // 센서 상태가 변화했는지 비교하려면 이전 상태를 저장할곳이 필요
  // Key: deviceCode_sensorKey, Value: lastEven("정상", "알람", "워닝")

  private final Map<String, String> lastState = new ConcurrentHashMap<>();

  public String getLastState(String sensorKey) {
    return lastState.get(sensorKey);
  }

  public void setLastState(String Key, String state) {
    lastState.put(Key, state);
  }
}
