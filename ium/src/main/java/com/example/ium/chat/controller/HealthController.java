package com.example.ium.chat.controller;

import com.example.ium._core.dto.Response;
import com.example.ium._core.exception.ErrorCode;
import com.example.ium._core.exception.IumApplicationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {
  @GetMapping("/health")
  public Response health() {
    return Response.success(Map.of("hello", "world"));
  }
  
  @GetMapping("/exception")
  public Response exception() {
    throw new IumApplicationException(ErrorCode.INTERNAL_SERVER_ERROR);
  }
  
}
