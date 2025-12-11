package com.example.SpringGroupBB.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WeatherService {
  private final RestTemplate restTemplate;

  // 기상청 초단기예보 raw값 추출
  public String getWeatherReport(String tmfc, String tmef, String vars) {
    // uri 생성
    UriComponentsBuilder uri = UriComponentsBuilder.fromHttpUrl("https://apihub.kma.go.kr/api/typ01/cgi-bin/url/nph-dfs_vsrt_grd")
            .queryParam("tmfc", tmfc)
            .queryParam("tmef", tmef)
            .queryParam("vars", vars)
            .queryParam("authKey", "naXVuwLKRgal1bsCylYG7A");

    // uri의 결과값 반환
    return restTemplate.getForObject(uri.toUriString(), String.class);
  }

  // raw값에서 실제 필요한 값 추출
  public String getWeatherResult(String rawRes) {
    System.out.println("rawData loading.");
    // 기상청의 지역 격자영역에 따른 배열 생성(세로253, 가로149)
    double[][] grid = new double[253][149];
    // 기상청이 보내준 rawRes의 한 덩이(149개)가 (20*7)+9개 형식으로 쪼개져있기 때문에 /r(줄바꿈)삭제하고 배열로 변경.
    String[] rawArray = rawRes.replace("\r", "").split(",");

    // 격자영역에 배열 담기.
    int cnt = 0;
    for(int i=0; i<253; i++) {
      for(int y=0; y<149; y++) {
        grid[i][y] = Double.parseDouble(rawArray[cnt]);
        cnt++;
      }
    }

    // 위성자표를 기상청의 지역 격자영역에 맞춘(청주=107, 69) 결과값을 반환
    return grid[107][69]+"";
  }

  public String getYellowTemperance() {
    System.out.println("rawData loading.");
    UriComponentsBuilder uri = UriComponentsBuilder.fromHttpUrl("http://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty")
            .queryParam("serviceKey", "0b4c5644b50280342c68cce0fac4dff1f61f2d14ea6af0459682a70626baf411")
            .queryParam("returnType", "json")
            .queryParam("numOfRows", "1")
            .queryParam("pageNo", "1")
            .queryParam("stationName", "봉명동")
            .queryParam("dataTerm", "DAILY")
            .queryParam("ver", "1.0");
    // 한글이 있기 때문에 UTF-8로 변환해준다
    URI uriStr = uri.build().encode(StandardCharsets.UTF_8).toUri();
    
    // 데이터 구조상 response, body, items에 들어가야만 데이터를 꺼내올 수 있다
    Map<String, Object> json = restTemplate.getForObject(uriStr, Map.class);
    Map<String, Object> response = (Map<String, Object>)json.get("response");
    Map<String, Object> body = (Map<String, Object>)response.get("body");
    // 데이터 구조상 최신 데이터 하나만 가져오더라도 List로 받은 후 0번째 데이터를 꺼내야한다
    List<Map<String, Object>> items = (List<Map<String, Object>>) body.get("items");
    // 최신 정보(1시간 간격 갱신)만 필요하기 때문에 0번째 데이터만 꺼내온다
    Map<String, Object> item = items.get(0);

    return item.get("pm10Value")+"/"+item.get("pm25Value");
  }
}
