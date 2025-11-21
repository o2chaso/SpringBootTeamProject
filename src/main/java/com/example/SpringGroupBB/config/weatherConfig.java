package com.example.SpringGroupBB.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class weatherConfig {
  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
}
