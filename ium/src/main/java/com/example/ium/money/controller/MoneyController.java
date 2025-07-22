package com.example.ium.money.controller;

import com.example.ium._core.dto.Response;
import com.example.ium.money.service.MoneyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class MoneyController {
  private final MoneyService moneyService;
  
  @GetMapping("/money/creditCharge")
  public String getCreditCharge(Model model) {
    return "/money/creditCharge";
  }
  
  @PostMapping("/money/creditCharge")
  @ResponseBody
  public Response createCreditCharge(@RequestBody int price, Principal principal) {
    String result = moneyService.createCreditCharge(principal.getName(), price);
    return Response.success(Map.of("redirectUrl", result));
  }
  
}
