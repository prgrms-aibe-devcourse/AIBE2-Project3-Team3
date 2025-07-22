package com.example.ium.recommend.controller;

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
    public String recommendChat(@RequestParam String category, Model model, Principal principal) {
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
    public Map<String, Object> getSuggestion(@RequestParam String category, 
                                           @RequestParam String message,
                                           Principal principal) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 실제로는 AI API 호출하겠지만, 현재는 카테고리별 더미 응답
            String suggestion = generateSuggestion(category, message);
            
            response.put("success", true);
            response.put("message", suggestion);
            response.put("category", category);
            
        } catch (Exception e) {
            log.error("AI 추천 중 오류 발생", e);
            response.put("success", false);
            response.put("message", "죄송합니다. 잠시 후 다시 시도해주세요.");
        }
        
        return response;
    }

    /**
     * 카테고리 코드를 한글명으로 변환
     */
    private String getCategoryName(String category) {
        return switch (category) {
            case "design" -> "디자인";
            case "marketing" -> "마케팅";
            case "programming" -> "IT·프로그래밍";
            case "video" -> "영상·사진·음향";
            default -> "기타";
        };
    }

    /**
     * 카테고리별 AI 추천 응답 생성 (임시 구현)
     * 실제로는 외부 AI API 호출
     */
    private String generateSuggestion(String category, String userMessage) {
        // 간단한 키워드 기반 응답 (실제로는 AI API 사용)
        return switch (category) {
            case "design" -> generateDesignSuggestion(userMessage);
            case "marketing" -> generateMarketingSuggestion(userMessage);
            case "programming" -> generateProgrammingSuggestion(userMessage);
            case "video" -> generateVideoSuggestion(userMessage);
            default -> "죄송합니다. 해당 분야에 대한 추천을 준비 중입니다.";
        };
    }

    private String generateDesignSuggestion(String message) {
        if (message.contains("로고") || message.contains("브랜딩")) {
            return "로고 디자인이나 브랜딩 작업을 원하시는군요! 다음과 같은 서비스를 추천드립니다:\n\n" +
                   "• 로고 디자인 + 브랜딩 가이드 패키지 (50만원~)\n" +
                   "• 명함 및 브랜딩 요소 디자인 (30만원~)\n" +
                   "• 브랜드 아이덴티티 전체 구축 (100만원~)\n\n" +
                   "어떤 업종이신지 알려주시면 더 구체적인 추천을 드릴 수 있어요!";
        } else if (message.contains("웹") || message.contains("UI") || message.contains("UX")) {
            return "웹 디자인이나 UI/UX 작업을 찾고 계시는군요! 이런 서비스는 어떠세요?\n\n" +
                   "• 반응형 웹사이트 디자인 (80만원~)\n" +
                   "• 모바일 앱 UI/UX 디자인 (60만원~)\n" +
                   "• 사용자 경험 개선 컨설팅 (40만원~)\n\n" +
                   "프로젝트 규모나 원하는 스타일이 있으시면 말씀해주세요!";
        } else {
            return "디자인 분야에 관심이 있으시는군요! 좀 더 구체적으로 어떤 디자인 작업이 필요하신지 알려주세요.\n\n" +
                   "예를 들어:\n• 로고나 브랜딩\n• 웹사이트 디자인\n• 인쇄물 디자인\n• 패키지 디자인\n\n" +
                   "어떤 분야에 관심이 있으신가요?";
        }
    }

    private String generateMarketingSuggestion(String message) {
        if (message.contains("SNS") || message.contains("소셜미디어")) {
            return "SNS 마케팅에 관심이 있으시는군요! 다음 서비스들을 추천드립니다:\n\n" +
                   "• 인스타그램 계정 운영 및 콘텐츠 제작 (월 80만원~)\n" +
                   "• 페이스북/인스타 광고 운영 (월 50만원~)\n" +
                   "• 브랜드 콘텐츠 기획 및 제작 (건당 30만원~)\n\n" +
                   "어떤 플랫폼을 주로 활용하고 싶으신가요?";
        } else if (message.contains("광고") || message.contains("퍼포먼스")) {
            return "온라인 광고 운영에 관심이 있으시는군요! 이런 서비스는 어떠세요?\n\n" +
                   "• 구글/네이버 검색광고 운영 (월 60만원~)\n" +
                   "• 페이스북/인스타 광고 대행 (월 50만원~)\n" +
                   "• 광고 성과 분석 및 최적화 (월 40만원~)\n\n" +
                   "예산 규모나 목표가 있으시면 알려주세요!";
        } else {
            return "마케팅 분야에 관심이 있으시는군요! 어떤 마케팅 서비스가 필요하신지 알려주세요.\n\n" +
                   "예를 들어:\n• SNS 마케팅\n• 온라인 광고 운영\n• 콘텐츠 마케팅\n• 브랜드 마케팅\n\n" +
                   "어떤 분야가 가장 시급하신가요?";
        }
    }

    private String generateProgrammingSuggestion(String message) {
        if (message.contains("웹사이트") || message.contains("홈페이지")) {
            return "웹사이트 개발을 원하시는군요! 다음과 같은 서비스를 추천드립니다:\n\n" +
                   "• 반응형 웹사이트 개발 (150만원~)\n" +
                   "• 쇼핑몰 구축 (300만원~)\n" +
                   "• 기업 홈페이지 제작 (100만원~)\n\n" +
                   "어떤 종류의 웹사이트가 필요하신지 자세히 알려주세요!";
        } else if (message.contains("앱") || message.contains("모바일")) {
            return "모바일 앱 개발에 관심이 있으시는군요! 이런 서비스는 어떠세요?\n\n" +
                   "• iOS/Android 네이티브 앱 개발 (500만원~)\n" +
                   "• 크로스플랫폼 앱 개발 (300만원~)\n" +
                   "• 앱 유지보수 및 업데이트 (월 50만원~)\n\n" +
                   "어떤 기능의 앱을 원하시는지 구체적으로 말씀해주세요!";
        } else {
            return "IT·프로그래밍 분야에 관심이 있으시는군요! 어떤 개발 서비스가 필요하신지 알려주세요.\n\n" +
                   "예를 들어:\n• 웹사이트/홈페이지 개발\n• 모바일 앱 개발\n• 시스템 개발\n• 데이터베이스 구축\n\n" +
                   "어떤 프로젝트를 계획하고 계신가요?";
        }
    }

    private String generateVideoSuggestion(String message) {
        if (message.contains("편집") || message.contains("영상편집")) {
            return "영상 편집 서비스를 찾고 계시는군요! 다음 서비스들을 추천드립니다:\n\n" +
                   "• 유튜브 영상 편집 (편당 15만원~)\n" +
                   "• 홍보영상 제작 및 편집 (300만원~)\n" +
                   "• 웨딩/행사 영상 편집 (50만원~)\n\n" +
                   "어떤 용도의 영상인지 알려주시면 더 정확한 추천을 드릴게요!";
        } else if (message.contains("촬영") || message.contains("제작")) {
            return "영상 촬영이나 제작에 관심이 있으시는군요! 이런 서비스는 어떠세요?\n\n" +
                   "• 기업 홍보영상 촬영+편집 (500만원~)\n" +
                   "• 제품 소개영상 제작 (200만원~)\n" +
                   "• 이벤트/행사 촬영 (100만원~)\n\n" +
                   "어떤 종류의 영상이 필요하신지 구체적으로 말씀해주세요!";
        } else {
            return "영상·사진·음향 분야에 관심이 있으시는군요! 어떤 서비스가 필요하신지 알려주세요.\n\n" +
                   "예를 들어:\n• 영상 편집\n• 영상 촬영\n• 사진 촬영\n• 음향 작업\n\n" +
                   "어떤 작업이 가장 필요하신가요?";
        }
    }
}