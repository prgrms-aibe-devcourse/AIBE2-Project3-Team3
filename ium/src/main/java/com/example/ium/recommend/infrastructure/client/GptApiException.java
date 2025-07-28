package com.example.ium.recommend.infrastructure.client;

/**
 * GPT API 호출 관련 예외
 */
public class GptApiException extends Exception {
    
    private final int statusCode;
    private final String errorCode;
    
    public GptApiException(String message) {
        super(message);
        this.statusCode = 0;
        this.errorCode = "UNKNOWN";
    }
    
    public GptApiException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = 0;
        this.errorCode = "UNKNOWN";
    }
    
    public GptApiException(String message, int statusCode, String errorCode) {
        super(message);
        this.statusCode = statusCode;
        this.errorCode = errorCode;
    }
    
    public GptApiException(String message, int statusCode, String errorCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.errorCode = errorCode;
    }
    
    public int getStatusCode() {
        return statusCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    /**
     * 재시도 가능한 오류인지 확인
     */
    public boolean isRetryable() {
        return statusCode >= 500 || statusCode == 429 || statusCode == 0;
    }
}
