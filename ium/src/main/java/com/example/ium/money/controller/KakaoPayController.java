package com.example.ium.money.controller;

import com.example.ium.money.dto.KakaoPayApproveDto;
import com.example.ium.money.service.KakaoPayService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class KakaoPayController {
  private final KakaoPayService kakaoPayService;
  
  @GetMapping(value = "/kakaopay/success")
  public String acceptPay(@RequestParam("pg_token") String pgToken) {
    KakaoPayApproveDto result = kakaoPayService.acceptPay(pgToken);
    return "redirect:/";
  }
}
