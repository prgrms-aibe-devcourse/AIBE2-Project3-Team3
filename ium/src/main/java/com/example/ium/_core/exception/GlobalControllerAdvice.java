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
  public ResponseEntity<?> handleIumException(IumApplicationException e) {
    log.error("Error occurs {}", e.toString());

    return ResponseEntity
            .status(e.getErrorCode().getStatus())
            .body(Response.error(e.getErrorCode().name()));
  }
  
  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<?> applicationHandler(RuntimeException e) {
    log.error("Error occurs {}", e.toString());

    return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Response.error(ErrorCode.INTERNAL_SERVER_ERROR.name()));
  }
}
