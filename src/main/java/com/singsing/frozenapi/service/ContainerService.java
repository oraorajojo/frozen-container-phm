package com.singsing.frozenapi.service;

import com.singsing.frozenapi.dto.ContainerRequestDTO;
import com.singsing.frozenapi.dto.ContainerResponseDTO;

import java.util.List;

// 컨테이너 관련 비즈니스 로직의 "규격(인터페이스)"만 정의
// 실제 구현은 ContainerServiceImpl이 담당한다. (UserService/UserServiceImpl과 같은 구조)
//
// User는 회원가입(생성)만 있었지만, Container는 마스터 데이터라 등록/조회/수정/삭제를 모두 열어둔 CRUD 형태
public interface ContainerService {

    ContainerResponseDTO register(ContainerRequestDTO containerRequestDTO); // 등록 (Create)
    List<ContainerResponseDTO> getList();                                  // 전체 목록 조회 (Read - all)
    ContainerResponseDTO get(Integer containerId);                            // 단건 조회 (Read - one)
    ContainerResponseDTO modify(Integer containerId, ContainerRequestDTO containerRequestDTO); // 수정 (Update)
    void remove(Integer containerId);                                        // 삭제 (Delete)

}
