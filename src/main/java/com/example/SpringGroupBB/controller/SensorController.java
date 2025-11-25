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
import com.example.SpringGroupBB.service.WeatherService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
  private final WeatherService weatherService;

  // Sensor Detailed Data
  @GetMapping("/sensorData")
  public String sensorDataGet(Model model) {
    model.addAttribute("userCsrf", true);
    return "sensor/sensorData";
  }

  // 상세 데이터(날짜 조회)
  @ResponseBody
  @GetMapping("/sensorSearch")
  public List<SensorDTO> sensorSearchGet(@RequestParam String startDate,
                                         @RequestParam String endDate,
                                         @RequestParam String deviceCode,
                                         @RequestParam String sensorKey,
                                         Model model) {
    String today = LocalDateTime.now().toString().substring(0, 10);
    model.getAttribute("userCsrf");
    model.addAttribute("today", today);
    return sensorService.getSensorHistory(startDate, endDate, deviceCode, sensorKey);
  }

  // Sensor 대시보드
  @GetMapping("/sensorList")
  public String sensorListGet(Model model) {
    model.addAttribute("userCsrf", true);
    model.addAttribute("toDay", LocalDate.now().toString());
    return "sensor/sensorList";
  }
  // 팝업창
  @GetMapping("/sensorPopup")
  public String sensorPopupGet(@RequestParam String deviceCode,
                               @RequestParam String sensorKey,
                               Model model) {
    List<ThresholdDTO> threshold = thresholdService.getThresholds();
    String sensorName = getSensorName(sensorKey);
    model.addAttribute("userCsrf", true);
    model.addAttribute("deviceCode", deviceCode);
    model.addAttribute("sensorKey", sensorKey);
    model.addAttribute("threshold", threshold);
    model.addAttribute("sensorName", sensorName);
    return "sensor/sensorPopup";
  }
  // 팝업창(인터락, 그래프, 로그) 버튼
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
  // 알람 로그
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
      case "value11" -> "소음";
      case "value12" -> "비접촉 온도";
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

  // 날씨API 시작
  @ResponseBody
  @PostMapping("/weatherReport")
  public String weatherReportPost(HttpServletRequest request, HttpServletResponse response) {
    String weatherReport = "";
    String cTimeValue = "";
    String cWeatherValue = "";
    Cookie[] cookies = request.getCookies();
    if(cookies != null) {
      for(int i=0; i<cookies.length; i++) {
        if(cookies[i].getName().equals("cTime")) cTimeValue = cookies[i].getValue();
        else if(cookies[i].getName().equals("cWeather")) cWeatherValue = cookies[i].getValue();
      }
    }
    // HHm으로 분의 앞부분만 가져오는 게 불가능해서 mm을 전부 써준다
    String toDay = LocalDateTime.now().minusMinutes(30).format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
    // 10분 간격 발표
    String tmfc = toDay.substring(0,11)+"0";

    if(!cTimeValue.equals(tmfc)) {
      // 기온 raw값
      String rawRes = weatherReportGet("T1H");
      // 기온 결과값
      weatherReport += weatherService.getWeatherResult(rawRes) + "/";
      // 구름(1맑음, 2구름많음, 4흐림) raw값
      rawRes = weatherReportGet("SKY");
      // 구름 결과값
      weatherReport += weatherService.getWeatherResult(rawRes) + "/";
      // 강수형태(0없음, 1비, 2눈, 3눈, 4소나기) raw값
      rawRes = weatherReportGet("PTY");
      // 강수형태 결과값
      weatherReport += weatherService.getWeatherResult(rawRes) + "/";
      // 미세먼지/초미세먼지 값
      weatherReport += weatherService.getYellowTemperance();

      Cookie weatherCookie = new Cookie("cWeather", weatherReport);
      weatherCookie.setPath("/");
      weatherCookie.setMaxAge(600 * 10);
      response.addCookie(weatherCookie);

      System.out.println("weatherReport: " + weatherReport);
      return weatherReport;
    }
    else {
      System.out.println("weatherReport: " + cWeatherValue);
      return cWeatherValue;
    }
  }
  @GetMapping("/weatherReport")
  public String weatherReportGet(String vars) {
    HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
    // HHm으로 분의 앞부분만 가져오는 게 불가능해서 mm을 전부 써준다
    String toDay = LocalDateTime.now().minusMinutes(30).format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
    // 10분 간격 발표
    String tmfc = toDay.substring(0,11)+"0";
    // 발표시간 기준 6시간 까지 1시간 간격으로 제공
    String tmef = toDay.substring(0,10);
    Cookie timeCookie = new Cookie("cTime", tmfc);
    timeCookie.setPath("/");
    timeCookie.setMaxAge(600*10);
    response.addCookie(timeCookie);
    return weatherService.getWeatherReport(tmfc, tmef, vars);
  }
  // 날씨API 끝
  // 일일 리포트 시작
  @GetMapping("/dailyReport")
  public String dailyReportGet(Model model,
                               @RequestParam(name = "deviceCode", defaultValue = "ENV_V2_1", required = false)String deviceCode,
                               @RequestParam(name = "measureDatetime", defaultValue = "", required = false)String measureDatetime,
                               @RequestParam(name = "flag", defaultValue = "0", required = false)int flag) {
    // 입력 받은 날짜 없으면 오늘.
    measureDatetime = measureDatetime.equals("")? LocalDate.now().toString():measureDatetime;
    // sensor의 min, avg, max값 검색.
    List<SensorDTO> sensorList = sensorService.selectSensorValueAndDate(measureDatetime, deviceCode, flag);

    // DB에 지정값이 없기 때문에 배열로 만들어서 html로 보내준다.
    String[] value = {"실내온도","상대습도","이산화탄소","유기화합물VOC","미세먼지","초미세먼지","온도_1","온도_2","온도_3","온도(비접촉)"};
    // DB검색결과.
    model.addAttribute("sensorList", sensorList);
    // 날짜.
    model.addAttribute("measureDatetime", measureDatetime);
    if(flag == 1) model.addAttribute("measureDatetimePast", LocalDate.parse(measureDatetime).minusDays(7).toString());
    else if(flag == 2) model.addAttribute("measureDatetimePast", LocalDate.parse(measureDatetime).minusMonths(1).toString());
    // 센서의 역할.
    model.addAttribute("value", value);
    // 찾아온 지역(1층, 2층...).
    model.addAttribute("deviceCode", deviceCode);
    // 일일, 주간, 월간.
    model.addAttribute("flag", flag);
    return "sensor/dailyReport";
  }
  // 일일 리포트 끝
}
