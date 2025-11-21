package com.example.SpringGroupBB.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

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
    System.out.println("raw데이터에서 값 추출중.");
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
}
