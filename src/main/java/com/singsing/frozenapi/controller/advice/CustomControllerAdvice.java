package com.singsing.frozenapi.controller.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

// 프로젝트 전역(모든 컨트롤러)에서 발생하는 특정 예외를 잡아,
// 사람이 읽기 좋은 JSON 에러 응답으로 변환해주는 클래스
//
// 이게 없다면? 예외가 그대로 터져서 스프링 기본 에러 페이지(스택트레이스 포함 500 에러 등)가 그대로 노출됨
@RestControllerAdvice // @ControllerAdvice + @ResponseBody. 모든 @RestController에서 발생하는 예외를 여기서 가로챈다
public class CustomControllerAdvice {

    // UserServiceImpl.signup()에서 이메일 중복 시 던지는 IllegalArgumentException을 처리
    // -> 이메일 중복은 "요청 자체는 형식상 맞지만, 이미 존재하는 자원과 충돌"하는 상황이므로 409 Conflict 사용
    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<?> handleIllegalArgument(IllegalArgumentException e) {
        // 응답 예시: { "msg": "이미 가입된 이메일입니다." }
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("msg", e.getMessage()));
    }

    // SignupRequestDTO의 @NotBlank, @Email, @Size 같은 Bean Validation 검증이 실패했을 때
    // Spring이 자동으로 던지는 MethodArgumentNotValidException을 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<?> handleValidation(MethodArgumentNotValidException e) {
        // 검증에 실패한 필드가 여러 개일 수 있으므로, 각 필드의 에러 메시지를 모아서 하나의 문자열로 합침
        // 예: "email: 이메일 형식이 올바르지 않습니다., password: 비밀번호는 8자 이상 64자 이하로 입력해주세요."
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("msg", message));
    }

}
