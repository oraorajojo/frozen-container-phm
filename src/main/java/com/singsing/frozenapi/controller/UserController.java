package com.singsing.frozenapi.controller;

import com.singsing.frozenapi.dto.SignupRequestDTO;
import com.singsing.frozenapi.dto.SignupResponseDTO;
import com.singsing.frozenapi.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 회원(User) 관련 HTTP 요청을 받는 컨트롤러
// 클라이언트(리액트 등) <-> 서버 사이의 진입점 역할만 하고, 실제 로직은 UserService에 위임한다.
@RestController                 // @Controller + @ResponseBody. 리턴값을 뷰(HTML)가 아니라 JSON으로 바로 응답
@RequestMapping("/api/users")   // 이 컨트롤러의 모든 API는 "/api/users"로 시작 (예: /api/users/signup)
@Slf4j
@RequiredArgsConstructor        // final 필드(userService)를 생성자로 주입받음
public class UserController {

    private final UserService userService;

    // 회원가입 API
    // 요청: POST http://localhost:8080/api/users/signup
    //       Body(JSON): { "email": "a@a.com", "password": "12345678" }
    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDTO> signup(
            @Valid @RequestBody SignupRequestDTO signupRequestDTO) {
        // @RequestBody  : HTTP 요청의 JSON body를 SignupRequestDTO 객체로 자동 변환(역직렬화)
        // @Valid        : SignupRequestDTO에 붙어있는 @NotBlank, @Email, @Size 등의 검증 어노테이션을 실행
        //                 -> 검증 실패 시 CustomControllerAdvice.handleValidation()이 처리하여 400 응답

        log.info("*********** UserController - signup: {}", signupRequestDTO);

        // 실제 가입 처리는 서비스 계층에 위임
        SignupResponseDTO responseDTO = userService.signup(signupRequestDTO);

        // 자원(회원)이 새로 생성되었으므로 HTTP 상태코드 201 Created + 생성된 회원 정보를 응답
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

}
