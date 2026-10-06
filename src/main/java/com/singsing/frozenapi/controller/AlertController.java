package com.singsing.frozenapi.controller;

import com.singsing.frozenapi.dto.AlertRequestDTO;
import com.singsing.frozenapi.dto.AlertResponseDTO;
import com.singsing.frozenapi.service.AlertService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 알림(Alert) 관련 REST API
// 실제 서비스에서는 예측/센서 로직이 이상을 감지했을 때 자동으로 호출하게 될 API지만,
// 그 자동 감지 로직(예측 도메인)은 아직 없어서 지금은 등록도 API로 직접 열어둔 상태.
@RestController
@RequestMapping("/api/alerts")
@Slf4j
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    // 등록 : POST /api/alerts
    @PostMapping
    public ResponseEntity<AlertResponseDTO> register(@Valid @RequestBody AlertRequestDTO alertRequestDTO) {
        log.info("*********** AlertController - register: {}", alertRequestDTO);
        AlertResponseDTO responseDTO = alertService.register(alertRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    // 전체 목록 조회 : GET /api/alerts
    @GetMapping
    public ResponseEntity<List<AlertResponseDTO>> list() {
        return ResponseEntity.ok(alertService.getList());
    }

    // 단건 조회 : GET /api/alerts/{alertId}
    @GetMapping("/{alertId}")
    public ResponseEntity<AlertResponseDTO> get(@PathVariable Integer alertId) {
        return ResponseEntity.ok(alertService.get(alertId));
    }

    // 읽음 처리 : PATCH /api/alerts/{alertId}/read
    @PatchMapping("/{alertId}/read")
    public ResponseEntity<AlertResponseDTO> markAsRead(@PathVariable Integer alertId) {
        log.info("*********** AlertController - markAsRead - alertId: {}", alertId);
        return ResponseEntity.ok(alertService.markAsRead(alertId));
    }

}
