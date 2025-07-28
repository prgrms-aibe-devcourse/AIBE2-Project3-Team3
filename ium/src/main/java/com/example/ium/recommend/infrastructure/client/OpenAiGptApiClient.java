package com.example.ium.recommend.infrastructure.client;

import com.example.ium.recommend.application.dto.request.GptRequestDto;
import com.example.ium.recommend.application.dto.response.GptResponseDto;
import com.example.ium.recommend.infrastructure.config.GptApiProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * OpenAI GPT API 클라이언트 구현체
 * RestTemplate을 사용하여 OpenAI API와 통신
 */
@Slf4j
@Component
public class OpenAiGptApiClient implements GptApiClient {
    
    private final RestTemplate gptRestTemplate;
    private final GptApiProperties gptApiProperties;
    private final ObjectMapper objectMapper;
    
    public OpenAiGptApiClient(@Qualifier("gptRestTemplate") RestTemplate gptRestTemplate,
                              GptApiProperties gptApiProperties,
                              ObjectMapper objectMapper) {
        this.gptRestTemplate = gptRestTemplate;
        this.gptApiProperties = gptApiProperties;
        this.objectMapper = objectMapper;
    }
    
    @Override
    public GptResponseDto sendRecommendationRequest(GptRequestDto request) throws GptApiException {
        log.info("GPT API 요청 시작 - model: {}, maxTokens: {}", request.getModel(), request.getMaxTokens());
        
        try {
            // API 키 유효성 검증
            if (!gptApiProperties.isValidApiKey()) {
                throw new GptApiException("유효하지 않은 API 키입니다", 401, "INVALID_API_KEY");
            }
            
            // 요청 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(gptApiProperties.getApiKey());
            headers.set("User-Agent", "ium-recommend-service/1.0");
            
            // 요청 페이로드 구성
            Map<String, Object> requestBody = buildRequestBody(request);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            // API 호출
            String url = gptApiProperties.getBaseUrl() + "/chat/completions";
            ResponseEntity<String> response = gptRestTemplate.exchange(
                    url, 
                    HttpMethod.POST, 
                    entity, 
                    String.class
            );
            
            // 응답 파싱
            GptResponseDto gptResponse = parseResponse(response.getBody());
            
            log.info("GPT API 요청 완료 - 응답 길이: {}", gptResponse.getOutputText().length());
            return gptResponse;
            
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("GPT API 호출 실패 - Status: {}, Body: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw mapHttpException(e);
        } catch (Exception e) {
            log.error("GPT API 요청 중 예기치 않은 오류 발생", e);
            throw new GptApiException("GPT API 요청 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean isHealthy() {
        try {
            // 간단한 health check 요청
            GptRequestDto testRequest = GptRequestDto.builder()
                    .model(gptApiProperties.getModel())
                    .input("Hello")
                    .maxTokens(5)
                    .temperature(0.1)
                    .build();
            
            sendRecommendationRequest(testRequest);
            return true;
        } catch (Exception e) {
            log.warn("GPT API 헬스체크 실패", e);
            return false;
        }
    }
    
    /**
     * GPT API 요청 바디 구성
     */
    private Map<String, Object> buildRequestBody(GptRequestDto request) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", request.getModel());
        requestBody.put("max_tokens", request.getMaxTokens());
        requestBody.put("temperature", request.getTemperature());
        
        // 메시지 형식으로 구성
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", "당신은 전문가와 클라이언트를 매칭해주는 AI 어시스턴트입니다. 주어진 정보를 바탕으로 적절한 추천을 제공하세요.");
        
        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", request.getInput());
        
        requestBody.put("messages", List.of(systemMessage, userMessage));
        
        return requestBody;
    }
    
    /**
     * API 응답 파싱
     */
    private GptResponseDto parseResponse(String responseBody) throws GptApiException {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            
            // 에러 응답 확인
            if (root.has("error")) {
                JsonNode error = root.get("error");
                String errorMessage = error.get("message").asText();
                String errorCode = error.get("code").asText();
                throw new GptApiException("GPT API 오류: " + errorMessage, 400, errorCode);
            }
            
            // 정상 응답 파싱
            JsonNode choices = root.get("choices");
            if (choices == null || choices.isEmpty()) {
                throw new GptApiException("응답에 choices가 없습니다", 500, "NO_CHOICES");
            }
            
            JsonNode firstChoice = choices.get(0);
            JsonNode message = firstChoice.get("message");
            String content = message.get("content").asText();
            String finishReason = firstChoice.get("finish_reason").asText();
            
            JsonNode usage = root.get("usage");
            Integer totalTokens = usage != null ? usage.get("total_tokens").asInt() : 0;
            
            return new GptResponseDto(content, finishReason, totalTokens);
            
        } catch (Exception e) {
            log.error("GPT API 응답 파싱 실패: {}", responseBody, e);
            throw new GptApiException("응답 파싱 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
    
    /**
     * HTTP 예외를 GPT API 예외로 변환
     */
    private GptApiException mapHttpException(Exception e) {
        if (e instanceof HttpClientErrorException clientError) {
            HttpStatus status = (HttpStatus) clientError.getStatusCode();
            String responseBody = clientError.getResponseBodyAsString();
            
            return switch (status) {
                case UNAUTHORIZED -> new GptApiException("인증 실패: API 키를 확인하세요", 401, "UNAUTHORIZED");
                case FORBIDDEN -> new GptApiException("접근 권한이 없습니다", 403, "FORBIDDEN");
                case TOO_MANY_REQUESTS -> new GptApiException("요청 한도를 초과했습니다", 429, "RATE_LIMIT_EXCEEDED");
                default -> new GptApiException("GPT API 클라이언트 오류: " + responseBody, status.value(), "CLIENT_ERROR");
            };
        } else if (e instanceof HttpServerErrorException serverError) {
            HttpStatus status = (HttpStatus) serverError.getStatusCode();
            return new GptApiException("GPT API 서버 오류", status.value(), "SERVER_ERROR");
        } else {
            return new GptApiException("HTTP 통신 오류: " + e.getMessage(), e);
        }
    }
}
