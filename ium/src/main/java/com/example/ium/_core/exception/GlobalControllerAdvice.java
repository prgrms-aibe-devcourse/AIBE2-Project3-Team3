package com.example.ium._core.exception;

import com.example.ium._core.dto.Response;
import com.example.ium.chat.controller.ChatController;
import com.example.ium.index.controller.IndexController;
import com.example.ium.member.application.controller.ExpertProfileController;
import com.example.ium.member.application.controller.MemberAuthController;
import com.example.ium.member.application.controller.MemberAuthViewController;
import com.example.ium.member.application.controller.MemberProfileController;
import com.example.ium.money.controller.MoneyController;
import com.example.ium.recommend.controller.RecommendController;
import com.example.ium.workrequest.controller.WorkRequestController;
import com.example.ium.workrequest.dto.WorkRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice(
        basePackages = {
        "com.example.ium.member",
        "com.example.ium.chat",
        "com.example.ium.workrequest",
        "com.example.ium.money",
        "com.example.ium.recommend",
        "com.example.ium.specialization",
        "com.example.ium.index"}
)
public class GlobalControllerAdvice {
  @ExceptionHandler(IumApplicationException.class)
  public ResponseEntity<?> handleIumException(IumApplicationException e, Model model) {
    model.addAttribute("errorMessage", e.getMessage());
    log.error("Error occurs {}", e.toString());

    return ResponseEntity
            .status(e.getErrorCode().getStatus())
            .body(Response.error(e.getErrorCode().name()));
  }
  
  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<?> applicationHandler(RuntimeException e, Model model) {
    model.addAttribute("errorMessage", "서버 오류가 발생했습니다.");
    log.error("Error occurs {}", e.toString());

    return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Response.error(ErrorCode.INTERNAL_SERVER_ERROR.name()));
  }
}
