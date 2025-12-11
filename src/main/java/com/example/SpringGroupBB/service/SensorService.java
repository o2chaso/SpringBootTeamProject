package com.example.SpringGroupBB.service;

import com.example.SpringGroupBB.dto.SensorDTO;
import com.example.SpringGroupBB.dto.ThresholdDTO;
import com.example.SpringGroupBB.entity.SensorEntity;
import com.example.SpringGroupBB.entity.ThresholdEntity;
import com.example.SpringGroupBB.repository.SensorRepository;
import com.example.SpringGroupBB.repository.ThresholdRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
  // 일일 리포트 시작
  @PersistenceContext
  EntityManager entityManager;
  // 일일 리포트 끝
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
      SensorDTO dto = SensorDTO.entityToDTO(entity);
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

  public List<SensorDTO> getSensorHistory(String startDate, String endDate, String deviceCode, String sensorKey) {
    startDate = startDate.replaceAll(" ", "T");
    endDate = endDate.replaceAll(" ", "T");

    // 날짜, 시간 문자열 -> LocalDateTime 변환
    LocalDateTime startTime = LocalDateTime.parse(startDate);
    LocalDateTime endTime = LocalDateTime.parse(endDate);

    // DB 조회
    List<SensorEntity> list = sensorRepository
            .findByDeviceCodeAndMeasureDatetimeBetweenOrderByMeasureDatetimeAsc(
                    deviceCode, startTime, endTime
            );
    List<SensorDTO> result = new ArrayList<>();
    for(SensorEntity entity : list) {
      SensorDTO dto = SensorDTO.entityToDTO(entity);
      ThresholdDTO th = null;
      // 임계값 조회
      Optional<ThresholdEntity> optional = thresholdRepository.findByDeviceCodeAndSensorKey(deviceCode, sensorKey);
      if(optional.isPresent()) {
        th = ThresholdDTO.entityToDto(optional.get());
      }
      dto.setThreshold(th); // 그래프용으로 임계값 포함
      result.add(dto);
    }
    return result;
  }

  // 일일 리포트 시작
  public List<SensorDTO> selectSensorValueAndDate(String measureDatetime, String deviceCode, int flag) {
    List<SensorDTO> sensorList = new ArrayList<>();
    String sql = "";
    String value = "value_";
    // 센서(value_1~10)의 최솟값, 평균값, 최댓값 산출.
    for(int i=1; i<=10; i++) {
      // 일일 리포트.
      if(flag == 0) sql = "SELECT ROUND(MIN("+value+i+"),2), ROUND(AVG("+value+i+"),2), ROUND(MAX("+value+i+"),2), " +
              "(SELECT COUNT(*) FROM event_log WHERE measure_datetime LIKE CONCAT('"+measureDatetime+"','%') AND device_code = '"+deviceCode+"' AND sensor_key = 'value"+i+"' AND event != 'Normal') AS eventData " +
              "FROM sensor WHERE measure_datetime LIKE CONCAT('"+measureDatetime+"','%') AND device_code = '"+deviceCode+"'";
        // 주간 리포트.
      else if(flag == 1) {
        if(i<2) measureDatetime = measureDatetime+"T23:59:59";
        // 시간까지 전부 표시되기 때문에 subString으로 자른 후, 시간을 자정으로 지정한다.
        String measureDatetimePast = LocalDateTime.parse(measureDatetime).minusDays(7).toString().substring(0,10)+" 00:00:00";
        sql = "SELECT ROUND(MIN("+value+i+"),2), ROUND(AVG("+value+i+"),2), ROUND(MAX("+value+i+"),2), " +
                "(SELECT COUNT(*) FROM event_log WHERE measure_datetime >= '"+measureDatetimePast+"' AND measure_datetime <= '"+measureDatetime+"' AND device_code = '"+deviceCode+"' AND sensor_key = 'value"+i+"' AND event != 'Normal') AS eventData " +
                "FROM sensor WHERE measure_datetime >= '"+measureDatetimePast+"' AND measure_datetime <= '"+measureDatetime+"' AND device_code = '"+deviceCode+"'";
      }
      else if(flag == 2) {
        // 시간까지 전부 표시되기 때문에 subString으로 자른 후, 시간을 자정으로 지정한다.
        if(i<2) measureDatetime = measureDatetime+"T23:59:59";
        String measureDatetimePast = LocalDateTime.parse(measureDatetime).minusMonths(1).toString().substring(0,10)+" 00:00:00";
        sql = "SELECT ROUND(MIN("+value+i+"),2), ROUND(AVG("+value+i+"),2), ROUND(MAX("+value+i+"),2), " +
                "(SELECT COUNT(*) FROM event_log WHERE measure_datetime >= '"+measureDatetimePast+"' AND measure_datetime <= '"+measureDatetime+"' AND device_code = '"+deviceCode+"' AND sensor_key = 'value"+i+"' AND event != 'Normal') AS eventData " +
                "FROM sensor WHERE measure_datetime >= '"+measureDatetimePast+"' AND measure_datetime <= '"+measureDatetime+"' AND device_code = '"+deviceCode+"'";
      }
      // 엔티티 매니저는 검색결과를 오브젝트로 주기 때문에 오브젝트로 받는다.
      Object[] result = (Object[]) entityManager.createNativeQuery(sql).getSingleResult();
      if(result[0] != null) {
        // 찾아온 값을 SensorDTO에 만든 사용자정의 생성자로 min, avg, max값 입력한다.
        SensorDTO dto = new SensorDTO(
                ((Number) result[0]).doubleValue(),
                ((Number) result[1]).doubleValue(),
                ((Number) result[2]).doubleValue(),
                ((Number) result[3]).intValue()
        );
        //List객체에 넣는다.
        sensorList.add(dto);
      }
    }
    return sensorList;
  }
  // 일일 리포트 끝
}
