package com.singsing.frozenapi.service;

import com.singsing.frozenapi.dto.AlertRequestDTO;
import com.singsing.frozenapi.dto.AlertResponseDTO;

import java.util.List;

// 알림 관련 비즈니스 로직의 규격만 정의
// alerts도 로그성 데이터라 수정/삭제 대신 "읽음 처리" 상태 변경 액션만 제공한다.
public interface AlertService {

    AlertResponseDTO register(AlertRequestDTO alertRequestDTO); // 등록 (Create)
    List<AlertResponseDTO> getList();                            // 전체 목록 조회
    AlertResponseDTO get(Integer alertId);                       // 단건 조회
    AlertResponseDTO markAsRead(Integer alertId);                // 읽음 처리

}
