package com.example.ium._core.exception;

import com.example.ium._core.dto.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
  public String handleIumException(IumApplicationException e) {
    log.error("예외 발생: {}", e.getErrorCode());

    ErrorCode code = e.getErrorCode();

    // 403
    if (code == ErrorCode.MEMBER_NOT_ACTIVE ||
            code == ErrorCode.MEMBER_NOT_FOUND ||
            code == ErrorCode.INVALID_REQUEST ) {
      return "redirect:/error/403";
    }

    // 404
    if (code == ErrorCode.EXPERT_PROFILE_NOT_FOUND ||
            code == ErrorCode.CHAT_ROOM_NOT_FOUND ||
            code == ErrorCode.MONEY_NOT_FOUND ||
            code == ErrorCode.WORK_REQUEST_NOT_FOUND ||
            code == ErrorCode.WORK_REQUEST_DOES_NOT_HAVE_EXPERT ||
            code == ErrorCode.REPORT_NOT_FOUND) {
      return "redirect:/error/404";
    }

    // 기본: 500
    return "redirect:/error/500";
  }
  
  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<?> applicationHandler(RuntimeException e) {
    log.error("Error occurs {}", e.toString());

    return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Response.error(ErrorCode.INTERNAL_SERVER_ERROR.name()));
  }
}
