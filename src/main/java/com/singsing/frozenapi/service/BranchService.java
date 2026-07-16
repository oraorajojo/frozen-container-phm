package com.singsing.frozenapi.service;

import com.singsing.frozenapi.dto.BranchRequestDTO;
import com.singsing.frozenapi.dto.BranchResponseDTO;
import com.singsing.frozenapi.dto.BranchStatusUpdateRequestDTO;

import java.util.List;

// 지점 관련 비즈니스 로직의 규격만 정의
// Container/Item과 달리 remove(삭제) API는 두지 않는다 - 회의록: "삭제 대신 폐점·휴점으로만 상태 변경"
public interface BranchService {

    BranchResponseDTO register(BranchRequestDTO branchRequestDTO);                         // 등록 (Create)
    List<BranchResponseDTO> getList();                                                     // 전체 목록 조회
    BranchResponseDTO get(Integer branchId);                                                // 단건 조회
    BranchResponseDTO modify(Integer branchId, BranchRequestDTO branchRequestDTO);          // 이름/주소 수정
    BranchResponseDTO changeStatus(Integer branchId, BranchStatusUpdateRequestDTO statusUpdateRequestDTO); // 상태 변경 (폐점/휴점/운영중)

}
