package com.example.ium.money.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "kakaopay")
@Getter
@Setter
public class KakaoPayProperties {
  
  private String cId;
  private String secretKey;
}
