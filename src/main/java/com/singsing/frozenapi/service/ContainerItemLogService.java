package com.singsing.frozenapi.service;

import com.singsing.frozenapi.dto.ContainerItemLogRequestDTO;
import com.singsing.frozenapi.dto.ContainerItemLogResponseDTO;

import java.util.List;

// 컨테이너 적재/출고 이력 관련 비즈니스 로직의 규격만 정의
// 로그(이력) 테이블 특성상 Container/Item과 달리 수정(Update)/삭제(Delete) API는 두지 않고,
// 등록(적재)/조회 + "출고 처리"라는 상태 변경 액션 하나만 제공한다.
public interface ContainerItemLogService {

    ContainerItemLogResponseDTO register(ContainerItemLogRequestDTO containerItemLogRequestDTO); // 적재 등록 (Create)
    List<ContainerItemLogResponseDTO> getList();                                                  // 전체 로그 조회
    ContainerItemLogResponseDTO get(Integer logId);                                                // 단건 조회
    ContainerItemLogResponseDTO unload(Integer logId);                                             // 출고 처리 (unloaded_at 채움)

}
