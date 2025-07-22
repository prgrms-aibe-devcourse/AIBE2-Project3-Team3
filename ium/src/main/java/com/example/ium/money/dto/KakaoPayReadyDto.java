package com.example.ium.money.dto;

import lombok.Data;

@Data
public class KakaoPayReadyDto {
  private String tid;
  private String next_redirect_app_url;
  private String next_redirect_mobile_url;
  private String next_redirect_pc_url;
  private String android_app_scheme;
  private String ios_app_scheme;
  private String create_at;
}
