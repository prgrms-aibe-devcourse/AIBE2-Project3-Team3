package com.example.ium._core.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class IumApplicationException extends RuntimeException {
  private ErrorCode errorCode;
  private String message;
  
  public IumApplicationException(ErrorCode errorCode) {
    this.errorCode = errorCode;
    this.message = null;
  }
  
  @Override
  public String getMessage() {
    if(message == null) {
      return errorCode.getMessage();
    }
    return String.format("%s. %s",errorCode.getMessage(), message);
  }
}
