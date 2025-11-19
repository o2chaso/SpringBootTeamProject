package com.example.SpringGroupBB.controller;

import com.example.SpringGroupBB.dto.ThresholdDTO;
import com.example.SpringGroupBB.entity.SensorEntity;
import com.example.SpringGroupBB.entity.ThresholdEntity;
import com.example.SpringGroupBB.service.SensorService;
import com.example.SpringGroupBB.service.ThresholdService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@Controller
@RequestMapping("/sensor")
@RequiredArgsConstructor
public class SensorController {

  private final SensorService sensorService;
  private final ThresholdService thresholdService;


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

    return "sensor/sensorPopup";
  }

  // 팝업창
  @GetMapping("/sensorPopup/detail")
  public String sensorPopupDetailGet(@RequestParam String tab,
                                     @RequestParam String deviceCode,
                                     @RequestParam String sensorKey,
                                     Model model) {

    String sensorName = getSeneorName(sensorKey);
    model.addAttribute("sensorName", sensorName);

    model.addAttribute("deviceCode", deviceCode);
    model.addAttribute("sensorKey", sensorKey);

    // Interlock
    if(tab.equals("interlock")) {
      // DB 조회
      Optional<ThresholdEntity> dto = thresholdService.findThreshold(deviceCode, sensorKey);
      model.addAttribute("threshold", dto);

      return "sensor/interlock";
    }

    // Graph
    if(tab.equals("graph")) {
      return "sensor/graph";
    }



    return "sensor/interlock";
  }

  private String getSeneorName(String sensorKey) {
    switch(sensorKey) {
      case "value1": return "실내온도";
      case "value2": return "상대습도";
      case "value3": return "이산화탄소";
      case "value4": return "유기화합물VOC";
      case "value5": return "초미세먼지 PM1.0";
      case "value6": return "초미세먼지 PM2.5";
      case "value7": return "초미세먼지 PM10";
      case "value8": return "온도_1)";
      case "value9": return "온도_2";
      case "value10": return "온도_3";
      case "value11": return "비접촉 온도";
      case "value12": return "소음";
      case "value13": return "노이즈";
      default: return sensorKey;
    }
  }


  // Interlock 설정
  @ResponseBody
  @PostMapping("/threshold/save")
  public String sensorThresholdSavePost(@RequestParam String deviceCode,
                                        @RequestParam String sensorKey,
                                        @RequestParam int alarm,
                                        @RequestParam int warning,
                                        @RequestParam int status) {
    System.out.println("==========================> 저장 요청");
    System.out.println("deviceCode: " + deviceCode);
    System.out.println("sensorKey: " + sensorKey);
    System.out.println("alarm: " + alarm);
    System.out.println("warning: " + warning);
    System.out.println("status: " + status);

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
