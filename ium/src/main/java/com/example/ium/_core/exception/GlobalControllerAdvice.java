package com.example.ium._core.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice(basePackages = {"com.example.ium.member", "com.example.ium.chat", "com.example.ium.workrequest"})
public class GlobalControllerAdvice {
  @ExceptionHandler(IumApplicationException.class)
  public String handleIumException(IumApplicationException e, Model model) {
    model.addAttribute("errorMessage", e.getMessage());
    log.error("Error occurs {}", e.toString());
    
    return "common/error";
  }
  
  @ExceptionHandler(RuntimeException.class)
  public String applicationHandler(RuntimeException e, Model model) {
    model.addAttribute("errorMessage", "서버 오류가 발생했습니다.");
    log.error("Error occurs {}", e.toString());

    return "common/error";
  }
}
