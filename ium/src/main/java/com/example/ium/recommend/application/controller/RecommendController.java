package com.example.ium.recommend.application.controller;

import com.example.ium.member.domain.model.Email;
import com.example.ium.member.domain.repository.MemberJPARepository;
import com.example.ium.recommend.domain.service.GptRecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/recommend")
public class RecommendController {
    
    private final GptRecommendationService gptRecommendationService;
    private final MemberJPARepository memberJPARepository;

    /**
     * AI 매칭 시작 페이지 - 분야 선택
     */
    @GetMapping
    public String recommendHome(Model model, Principal principal) {
        if (principal != null) {
            model.addAttribute("username", principal.getName());
        }
        return "recommend/recommend";
    }

    /**
     * 선택한 분야로 대화형 추천 페이지 이동
     */
    @GetMapping("/chat")
    public String recommendChat(@RequestParam(name = "category", required = false, defaultValue = "design") String category, Model model, Principal principal) {
        model.addAttribute("category", category);
        model.addAttribute("categoryName", getCategoryName(category));
        
        if (principal != null) {
            model.addAttribute("username", principal.getName());
        }
        
        return "recommend/recommend-chat";
    }

    /**
     * AI 추천 응답 API
     */
    @PostMapping("/suggest")
    @ResponseBody
    public Map<String, Object> getSuggestion(@RequestParam("category") String category, 
                                           @RequestParam("message") String message,
                                           Principal principal) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (principal == null) {
                response.put("success", false);
                response.put("message", "로그인이 필요합니다.");
                return response;
            }
            
            // 사용자 정보 조회
            String userEmail = principal.getName();
            Long memberId = memberJPARepository.findByEmail(Email.of(userEmail))
                    .map(member -> member.getId())
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
            
            // GPT API를 통한 AI 매칭 추천
            Map<String, Object> recommendationResult = gptRecommendationService.getAIExpertRecommendation(
                    memberId, category, message);
            
            response.put("success", true);
            response.put("message", recommendationResult.get("message"));
            response.put("expertInfo", recommendationResult.get("expertInfo"));
            response.put("category", category);
            
        } catch (Exception e) {
            log.error("AI 추천 중 오류 발생", e);
            response.put("success", false);
            response.put("message", "죄송합니다. 잠시 후 다시 시도해주세요. 오류: " + e.getMessage());
        }
        
        return response;
    }

    /**
     * 카테고리 코드를 한글명으로 변환
     */
    private String getCategoryName(String category) {
        return switch (category) {
            case "design" -> "디자인";
            case "programming" -> "프로그래밍";
            case "video" -> "영상편집";
            case "legal" -> "세무/법무/노무";
            case "translation" -> "번역/통역";
            default -> "기타";
        };
    }
}