package com.singsing.frozenapi.service;

import com.singsing.frozenapi.dto.SignupRequestDTO;
import com.singsing.frozenapi.dto.SignupResponseDTO;

// 회원 관련 비즈니스 로직의 "규격(인터페이스)"만 정의
// 실제 구현은 UserServiceImpl이 담당한다.
//
// 인터페이스와 구현체를 분리하는 이유:
// - 컨트롤러 등 사용하는 쪽에서는 인터페이스(UserService)만 알면 되고, 구현 세부사항(UserServiceImpl)에 의존하지 않게 됨
// - 나중에 구현을 바꾸거나(예: 테스트용 가짜 구현) 여러 구현체를 둘 때 유연하게 대응 가능
public interface UserService {

    // 회원가입 처리: 요청 DTO를 받아서 가입 처리 후, 응답 DTO를 리턴
    SignupResponseDTO signup(SignupRequestDTO signupRequestDTO);

}
