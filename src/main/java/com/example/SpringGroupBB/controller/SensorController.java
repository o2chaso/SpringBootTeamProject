package com.example.SpringGroupBB.controller;

import com.example.SpringGroupBB.common.EventStateHolder;
import com.example.SpringGroupBB.dto.EventLogDTO;
import com.example.SpringGroupBB.dto.SensorDTO;
import com.example.SpringGroupBB.dto.ThresholdDTO;
import com.example.SpringGroupBB.entity.EventLog;
import com.example.SpringGroupBB.entity.SensorEntity;
import com.example.SpringGroupBB.entity.ThresholdEntity;
import com.example.SpringGroupBB.repository.EventLogRepository;
import com.example.SpringGroupBB.repository.SensorRepository;
import com.example.SpringGroupBB.service.EventLogService;
import com.example.SpringGroupBB.service.SensorService;
import com.example.SpringGroupBB.service.ThresholdService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

@Controller
@RequestMapping("/sensor")
@RequiredArgsConstructor
public class SensorController {

  private final EventStateHolder eventStateHolder;
  private final SensorService sensorService;
  private final EventLogService eventLogService;
  private final ThresholdService thresholdService;
  private final SensorRepository sensorRepository;
  private final EventLogRepository eventLogRepository;


  @GetMapping("/sensorList")
  public String sensorListGet(Model model) {
    model.addAttribute("userCsrf", true);
    return "sensor/sensorList";
  }

  @GetMapping("/sensorInterlock")
  public String sensorInterlockGet(Model model) {
    model.addAttribute("userCsrf", true);

    List<ThresholdDTO> dto = thresholdService.getThresholds();
    model.addAttribute("dto", dto);

    return "sensor/sensorInterlock";
  }

  @GetMapping("/sensorPopup")
  public String sensorPopupGet(@RequestParam String deviceCode,
                               @RequestParam String sensorKey,
                               Model model) {
    model.addAttribute("userCsrf", true);

    model.addAttribute("deviceCode", deviceCode);
    model.addAttribute("sensorKey", sensorKey);

    List<ThresholdDTO> threshold = thresholdService.getThresholds();
    model.addAttribute("threshold", threshold);
    System.out.println("=======================================" + deviceCode + " / " + sensorKey + "threshold" + threshold);

    String sensorName = getSensorName(sensorKey);
    model.addAttribute("sensorName", sensorName);

    return "sensor/sensorPopup";
  }

  // 팝업창
  @GetMapping("/sensorPopup/detail")
  public String sensorPopupDetailGet(@RequestParam String tab,
                                     @RequestParam String deviceCode,
                                     @RequestParam String sensorKey,
                                     Model model) {

    String sensorName = getSensorName(sensorKey);
    model.addAttribute("sensorName", sensorName);
    model.addAttribute("deviceCode", deviceCode);
    model.addAttribute("sensorKey", sensorKey);

    switch (tab) {
      case "interlock" -> {
        // DB 조회
        Optional<ThresholdEntity> dto = thresholdService.findThreshold(deviceCode, sensorKey);
        model.addAttribute("threshold", dto);
        return "sensor/interlock :: content";
      }

      // Graph
      case "graph" -> {
        return "sensor/graph :: content";
      }

      // EventLog
      case "eventLog" -> {
        return "sensor/eventLog :: content";
      }
    }
    return "sensor/interlock :: content";
  }

  // 과거 데이터 그래프
  @ResponseBody
  @GetMapping("/sensorPopup/history")
  public List<SensorDTO> getHistory(
          @RequestParam("deviceCode") String deviceCode,
          @RequestParam("sensorKey") String sensorKey,
          @RequestParam("nowTime") String nowTime) {

    return sensorService.getPastSensorData(deviceCode, sensorKey, nowTime);
  }

  @ResponseBody
  @GetMapping("/eventLog/list")
  public List<EventLogDTO> eventLogListGet(@RequestParam("deviceCode") String deviceCode,
                                           @RequestParam("sensorKey") String sensorKey) {
    List<EventLog> list = eventLogRepository.findTop20ByDeviceCodeAndSensorKeyOrderByMeasureDatetimeDesc(deviceCode, sensorKey);
    List<EventLogDTO> result = new ArrayList<>();
    for(EventLog log : list) {
      result.add(EventLogDTO.entityToDto(log));
    }
    return result;
  }

  // 센서명 매핑
  private String getSensorName(String sensorKey) {
    return switch (sensorKey) {
      case "value1" -> "실내온도";
      case "value2" -> "상대습도";
      case "value3" -> "이산화탄소";
      case "value4" -> "유기화합물VOC";
      case "value5" -> "초미세먼지 PM1.0";
      case "value6" -> "초미세먼지 PM2.5";
      case "value7" -> "초미세먼지 PM10";
      case "value8" -> "온도_1)";
      case "value9" -> "온도_2";
      case "value10" -> "온도_3";
      case "value11" -> "비접촉 온도";
      case "value12" -> "소음";
      case "value13" -> "조도";
      default -> sensorKey;
    };
  }

  // Interlock 설정
  @ResponseBody
  @PostMapping("/threshold/save")
  public String sensorThresholdSavePost(@RequestParam String deviceCode,
                                        @RequestParam String sensorKey,
                                        @RequestParam int alarm,
                                        @RequestParam int warning,
                                        @RequestParam int status) {

    ThresholdDTO dto = ThresholdDTO.builder()
            .deviceCode(deviceCode)
            .sensorKey(sensorKey)
            .alarm(alarm)
            .warning(warning)
            .status(status)
            .build();

    thresholdService.saveThreshold(dto);

    return "OK";
  }

  /* SSE Controller */
  // 참고: https://jforj.tistory.com/419 / https://back-stead.tistory.com/105 / https://devel-repository.tistory.com/31
  @GetMapping(value = "/sensorList/sse", produces = "text/event-stream")
  public SseEmitter sensorListSse(Model model) {
    model.addAttribute("userCsrf", true);
    SseEmitter emitter = new SseEmitter(0L);

    final boolean[] alive = {true};

    // 연결 완료
    emitter.onCompletion(new Runnable() {
      @Override
      public void run() {
        alive[0] = false;
      }
    });
    // 타임 아웃
    emitter.onTimeout(new Runnable() {
      @Override
      public void run() {
        alive[0] = false;
      }
    });
    // 오류 발생
    emitter.onError(new Consumer<Throwable>() {
      @Override
      public void accept(Throwable throwable) {
        alive[0] = false;
      }
    });
    
    new Thread(() -> {
      try {
        while (alive[0]) {

          // 최신 전체 센서
          List<SensorEntity> sensor = sensorService.getSensorList();
          // Interlock 가져오기
          List<ThresholdDTO> thresholdList = thresholdService.getThresholds();

          // 이벤트 로그 저장
          for(SensorEntity s : sensor) {
            String deviceCode = s.getDeviceCode();
            // value1~13 전부 체크

            for (int i = 1; i <= 13; i++) {
              String sensorKey = "value" + i;

              // 센서 값 꺼내기(null이면 skip)
              Double value = null;
              try {
                value = (Double) SensorEntity.class
                        .getMethod("getValue" + i) // SensorEntity 클래스에서 getValue + i 이름의 메서드를 찾음
                        .invoke(s);                // 찾은 메서드를 실제 SensorEntity 객체 s에 대해 실행
              } catch (Exception e) {
                continue;                          // i = null 이면 건너뛰고 다시 실행
              }
              if (value == null) continue;          // SensorEntity의 value = null 이면 건너뛰고 다시 실행

            // 센서 키에 맞는 임계치 찾기
            ThresholdDTO th = thresholdList.stream()
                    .filter(t -> t.getDeviceCode().equals(deviceCode) && t.getSensorKey().equals(sensorKey))
                    .findFirst()
                    .orElse(null);
              // 현재 상테 계산
              String currentState = eventLogService.checkSensorState(value, th);

              // 이전 상태 가져오기
              String key = deviceCode + "_" + sensorKey;
              String prevState = eventStateHolder.getLastState(key);

              // 처음이거나, 상태가 변경된 경우에만 저장
              if(prevState == null || !prevState.equals(currentState)) {
                eventLogService.saveEventLog(deviceCode, sensorKey, value, currentState);
                eventStateHolder.setLastState(key, currentState);
              }
            }
          }
          
          // SSE로 보내기
          Map<String, Object> map = new HashMap<>();
          map.put("sensor", sensor);
          map.put("threshold", thresholdList);

          try {
            emitter.send(SseEmitter.event().data(map));
            Thread.sleep(2000);
          } catch (IOException e) {
            // 클라이언트 연결 끊길 시 send 반복 중단
            alive[0] = false;
            break;
          }
        }
      } catch (Exception e) {
        // emitter 종료
        emitter.complete();
      }
    }).start();
    return emitter;
  }


}
