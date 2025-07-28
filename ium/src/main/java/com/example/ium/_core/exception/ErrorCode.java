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
  EXPERT_PROFILE_NOT_FOUND(HttpStatus.NOT_FOUND, "전문가 프로필을 찾을 수 없습니다."),
  EXPERT_PROFILE_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 전문가 프로필이 존재합니다."),
  CHAT_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "채팅방을 찾을 수 없습니다."),
  MONEY_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 머니를 찾을 수 없습니다."),
  WORK_REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 의뢰를 찾을 수 없습니다."),
  WORK_REQUEST_DOES_NOT_HAVE_EXPERT(HttpStatus.NOT_FOUND, "해당 의뢰는 아직 전문가가 할당되지 않았습니다."),
  REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "신고를 찾을 수 없습니다."),
  MEMBER_NOT_ACTIVE(HttpStatus.FORBIDDEN, "활동 가능한 회원이 아닙니다.")
  ;
  
  private HttpStatus status;
  private String message;
}