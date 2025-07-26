package com.example.ium._core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

@Configuration
public class SpringConfig {
  
  @Bean
  @Primary
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
}
