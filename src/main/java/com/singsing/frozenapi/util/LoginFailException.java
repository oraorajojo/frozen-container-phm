package com.singsing.frozenapi.util;

// 로그인 실패(이메일 없음 / 비밀번호 불일치 / 승인 대기 상태 등) 시 던지는 예외
// CustomControllerAdvice에서 401 Unauthorized로 변환됨
public class LoginFailException extends RuntimeException {
    public LoginFailException(String message) {
        super(message);
    }
}
