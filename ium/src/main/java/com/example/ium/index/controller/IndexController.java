package com.example.ium.index.controller;

import com.example.ium._core.dto.Response;
import com.example.ium._core.exception.ErrorCode;
import com.example.ium._core.exception.IumApplicationException;
import com.example.ium.workrequest.entity.WorkRequestEntity;
import com.example.ium.workrequest.service.WorkRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class IndexController {
  
  private final WorkRequestService workRequestService;
  
  @GetMapping("/")
  public String home(Model model, 
                     @RequestParam(value = "category", required = false) String category,
                     @RequestParam(value = "search", required = false) String search) {
    
    List<WorkRequestEntity> workRequests;
    
    // 카테고리와 검색 조건에 따라 데이터 가져오기
    if (search != null && !search.trim().isEmpty()) {
      if (category != null && !category.trim().isEmpty() && !"all".equals(category)) {
        // 카테고리 + 검색
        workRequests = workRequestService.getRequestsByCategoryAndSearch(category, search);
      } else {
        // 검색만
        workRequests = workRequestService.searchRequests(search);
      }
    } else if (category != null && !category.trim().isEmpty() && !"all".equals(category)) {
      // 카테고리만
      workRequests = workRequestService.getRequestsByCategory(category);
    } else {
      // 전체
      workRequests = workRequestService.getAllRequests();
    }
    
    model.addAttribute("workRequests", workRequests);
    model.addAttribute("currentCategory", category != null ? category : "all");
    model.addAttribute("currentSearch", search != null ? search : "");
    
    return "index";
  }
  
  @GetMapping("/index/exception")
  @ResponseBody
  public Response exception() {
    throw new IumApplicationException(ErrorCode.INTERNAL_SERVER_ERROR);
  }
  
}