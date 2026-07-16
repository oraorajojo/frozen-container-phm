package com.singsing.frozenapi.controller;

import com.singsing.frozenapi.dto.BranchRequestDTO;
import com.singsing.frozenapi.dto.BranchResponseDTO;
import com.singsing.frozenapi.dto.BranchStatusUpdateRequestDTO;
import com.singsing.frozenapi.service.BranchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 지점(Branch) 관련 REST API
// Container/Item과 달리 DELETE 엔드포인트는 없다 - 회의록: "삭제 대신 폐점·휴점으로만 상태 변경"
@RestController
@RequestMapping("/api/branches")
@Slf4j
@RequiredArgsConstructor
public class BranchController {

    private final BranchService branchService;

    // 등록 : POST /api/branches
    @PostMapping
    public ResponseEntity<BranchResponseDTO> register(@Valid @RequestBody BranchRequestDTO branchRequestDTO) {
        log.info("*********** BranchController - register: {}", branchRequestDTO);
        BranchResponseDTO responseDTO = branchService.register(branchRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    // 전체 목록 조회 : GET /api/branches
    // 프론트에서 회원가입/컨테이너 등록 시 지점 선택 드롭다운을 채울 때 사용
    @GetMapping
    public ResponseEntity<List<BranchResponseDTO>> list() {
        return ResponseEntity.ok(branchService.getList());
    }

    // 단건 조회 : GET /api/branches/{branchId}
    @GetMapping("/{branchId}")
    public ResponseEntity<BranchResponseDTO> get(@PathVariable Integer branchId) {
        return ResponseEntity.ok(branchService.get(branchId));
    }

    // 지점명/주소 수정 : PUT /api/branches/{branchId}
    @PutMapping("/{branchId}")
    public ResponseEntity<BranchResponseDTO> modify(
            @PathVariable Integer branchId,
            @Valid @RequestBody BranchRequestDTO branchRequestDTO) {
        log.info("*********** BranchController - modify - branchId: {}", branchId);
        return ResponseEntity.ok(branchService.modify(branchId, branchRequestDTO));
    }

    // 상태 변경(운영중/폐점/휴점) : PATCH /api/branches/{branchId}/status
    // Body(JSON): { "status": "CLOSED" }
    @PatchMapping("/{branchId}/status")
    public ResponseEntity<BranchResponseDTO> changeStatus(
            @PathVariable Integer branchId,
            @Valid @RequestBody BranchStatusUpdateRequestDTO statusUpdateRequestDTO) {
        log.info("*********** BranchController - changeStatus - branchId: {}", branchId);
        return ResponseEntity.ok(branchService.changeStatus(branchId, statusUpdateRequestDTO));
    }

}
