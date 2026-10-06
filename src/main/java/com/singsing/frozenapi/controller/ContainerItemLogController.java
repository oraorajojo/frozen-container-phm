package com.singsing.frozenapi.controller;

import com.singsing.frozenapi.dto.ContainerItemLogRequestDTO;
import com.singsing.frozenapi.dto.ContainerItemLogResponseDTO;
import com.singsing.frozenapi.service.ContainerItemLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 컨테이너 적재/출고 이력(container_item_log) 관련 API
// Container/Item과 달리 로그 테이블 특성상 임의 수정/삭제 API는 두지 않고,
// 등록(적재)/조회 + "출고 처리(unload)"라는 상태 변경 액션 하나만 제공한다.
@RestController
@RequestMapping("/api/container-item-logs")
@Slf4j
@RequiredArgsConstructor
public class ContainerItemLogController {

    private final ContainerItemLogService containerItemLogService;

    // 적재 등록 : POST /api/container-item-logs
    @PostMapping
    public ResponseEntity<ContainerItemLogResponseDTO> register(@Valid @RequestBody ContainerItemLogRequestDTO containerItemLogRequestDTO) {
        log.info("*********** ContainerItemLogController - register: {}", containerItemLogRequestDTO);
        ContainerItemLogResponseDTO responseDTO = containerItemLogService.register(containerItemLogRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    // 전체 목록 조회 : GET /api/container-item-logs
    @GetMapping
    public ResponseEntity<List<ContainerItemLogResponseDTO>> list() {
        return ResponseEntity.ok(containerItemLogService.getList());
    }

    // 단건 조회 : GET /api/container-item-logs/{logId}
    @GetMapping("/{logId}")
    public ResponseEntity<ContainerItemLogResponseDTO> get(@PathVariable Integer logId) {
        return ResponseEntity.ok(containerItemLogService.get(logId));
    }

    // 출고 처리 : PATCH /api/container-item-logs/{logId}/unload
    // PUT이 아니라 PATCH를 쓴 이유: 전체 리소스를 교체하는 게 아니라 unloaded_at 하나만 부분적으로 바꾸는 상태 전이이기 때문
    @PatchMapping("/{logId}/unload")
    public ResponseEntity<ContainerItemLogResponseDTO> unload(@PathVariable Integer logId) {
        log.info("*********** ContainerItemLogController - unload - logId: {}", logId);
        return ResponseEntity.ok(containerItemLogService.unload(logId));
    }

}
