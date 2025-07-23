package com.example.ium.money.service;

import com.example.ium._core.exception.ErrorCode;
import com.example.ium._core.exception.IumApplicationException;
import com.example.ium.money.domain.model.Money;
import com.example.ium.money.domain.repository.MoneyRepository;
import com.example.ium.money.dto.KakaoPayApproveDto;
import com.example.ium.money.dto.KakaoPayReadyDto;
import com.example.ium.money.properties.KakaoPayProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KakaoPayService {
  
  private final RestTemplate restTemplate;
  private final KakaoPayProperties kakaoPayProperties;
  private final MoneyRepository moneyRepository;
  
  private KakaoPayReadyDto kakaoPayReady;
  private String email;
  private Money money;
  
  private HttpHeaders getHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.set(HttpHeaders.AUTHORIZATION, "SECRET_KEY " + kakaoPayProperties.getSecretKey());
    headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    return headers;
  }
  
  public String readyPay(Long orderId, String email) {
    this.email = email;
    Money money = moneyRepository.findById(orderId)
            .orElseThrow(() -> new IumApplicationException(ErrorCode.MONEY_NOT_FOUND));
    this.money = money;
    
    Map<String, String> params = new HashMap<>();
    params.put("cid", kakaoPayProperties.getCId());
    params.put("partner_order_id", String.valueOf(orderId));
    params.put("partner_user_id", email);
    params.put("item_name", "credit");
    params.put("quantity", String.valueOf(1));
    params.put("total_amount", String.valueOf(money.getPrice()));
    params.put("vat_amount", String.valueOf(money.getPrice()/10));
    params.put("tax_free_amount", "0");
    params.put("approval_url", "http://localhost:8080/kakaopay/success");
    params.put("fail_url", "http://localhost:8080/kakaopay/fail");
    params.put("cancel_url", "http://localhost:8080/kakaopay/cancel");
    
    HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(params, this.getHeaders());
    
    kakaoPayReady = restTemplate.postForObject("https://open-api.kakaopay.com/online/v1/payment/ready", requestEntity, KakaoPayReadyDto.class);
    return kakaoPayReady.getNext_redirect_pc_url();
  }
  
  
  public KakaoPayApproveDto acceptPay(String pgToken) {
    Map<String, String> params = new HashMap<>();
    params.put("cid", kakaoPayProperties.getCId());
    params.put("tid", kakaoPayReady.getTid());
    params.put("partner_order_id", String.valueOf(money.getId()));
    params.put("partner_user_id", email);
    params.put("pg_token", pgToken);
    
    HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(params, this.getHeaders());
    
    KakaoPayApproveDto result = restTemplate.postForObject("https://open-api.kakaopay.com/online/v1/payment/approve", requestEntity, KakaoPayApproveDto.class);
    
    return result;
  }
}
