package com.example.ium.money;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@PropertySource("classpath:/config/application-local.yml")
@Component
@ConfigurationProperties(prefix = "kakaopay")
@Getter
@Setter
public class KakaoPayProperties {
  
  private String cId;
  private String secretKey;
}
