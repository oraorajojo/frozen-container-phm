package com.singsing.frozenapi.util;

// JWT 검증(파싱) 실패 시 던지는 예외 (만료/서명불일치/형식오류 등)
// CustomControllerAdvice에서 401 Unauthorized로 변환됨
public class CustomJWTException extends RuntimeException {
    public CustomJWTException(String message) {
        super(message);
    }
}
