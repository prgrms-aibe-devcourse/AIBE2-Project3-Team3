package com.example.ium.mainpage.controller;

import com.example.ium._core.dto.Response;
import com.example.ium._core.exception.ErrorCode;
import com.example.ium._core.exception.IumApplicationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainpageController {
  
  @GetMapping("/")
  public String home() {
    return "mainpage/index";
  }
  
  @GetMapping("/mainpage")  // 혹시 몰라서 해당 경로도 매핑
  public String mainpage() {
    return "mainpage/index";
  }
  
  @GetMapping("/mainpage/exception")
  public Response exception() {
    throw new IumApplicationException(ErrorCode.INTERNAL_SERVER_ERROR);
  }
  
}