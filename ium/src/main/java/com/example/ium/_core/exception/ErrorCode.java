package com.example.ium._core.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"),
  MEMBER_NOT_FOUND(HttpStatus.UNAUTHORIZED, "해당 회원을 찾을 수 없습니다."),
  INVALID_REQUEST(HttpStatus.BAD_REQUEST, "요청이 올바르지 않습니다."),
  DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
  CHAT_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "채팅방을 찾을 수 없습니다."),
  MONEY_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 머니를 찾을 수 없습니다."),
  ;
  
  private HttpStatus status;
  private String message;
}