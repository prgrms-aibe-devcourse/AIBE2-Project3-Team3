package com.example.ium.index.controller;

import com.example.ium._core.dto.Response;
import com.example.ium._core.exception.ErrorCode;
import com.example.ium._core.exception.IumApplicationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IndexController {
  
  @GetMapping("/")
  public String home() {
    return "index";
  }
  
  @GetMapping("/index/exception")
  @ResponseBody
  public Response exception() {
    throw new IumApplicationException(ErrorCode.INTERNAL_SERVER_ERROR);
  }
  
}