package com.singsing.frozenapi.service;

import com.singsing.frozenapi.domain.Alert;
import com.singsing.frozenapi.dto.AlertRequestDTO;
import com.singsing.frozenapi.dto.AlertResponseDTO;
import com.singsing.frozenapi.repository.AlertRepository;
import com.singsing.frozenapi.repository.ContainerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class AlertServiceImpl implements AlertService {

    private final AlertRepository alertRepository;
    private final ContainerRepository containerRepository; // container_id FK 존재 여부 검증용

    @Override
    public AlertResponseDTO register(AlertRequestDTO alertRequestDTO) {
        log.info("*********** AlertService - register: {}", alertRequestDTO);

        if (!containerRepository.existsById(alertRequestDTO.getContainerId())) {
            throw new NoSuchElementException("존재하지 않는 컨테이너입니다.");
        }

        // isRead는 Alert.java의 @Builder.Default로 항상 false에서 시작, created_at은 @CreatedDate가 자동으로 채움
        Alert alert = Alert.builder()
                .containerId(alertRequestDTO.getContainerId())
                .source(alertRequestDTO.getSource())
                .grade(alertRequestDTO.getGrade())
                .message(alertRequestDTO.getMessage())
                .build();

        Alert saved = alertRepository.save(alert);
        return entityToDTO(saved);
    }

    @Override
    public List<AlertResponseDTO> getList() {
        return alertRepository.findAll().stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AlertResponseDTO get(Integer alertId) {
        return entityToDTO(getAlertOrThrow(alertId));
    }

    @Override
    public AlertResponseDTO markAsRead(Integer alertId) {
        log.info("*********** AlertService - markAsRead - alertId: {}", alertId);
        Alert alert = getAlertOrThrow(alertId);
        alert.markAsRead(); // 영속 상태 엔티티라 dirty checking으로 자동 UPDATE
        return entityToDTO(alert);
    }

    private Alert getAlertOrThrow(Integer alertId) {
        return alertRepository.findById(alertId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 알림입니다."));
    }

    private AlertResponseDTO entityToDTO(Alert alert) {
        return AlertResponseDTO.builder()
                .alertId(alert.getAlertId())
                .containerId(alert.getContainerId())
                .source(alert.getSource().name())
                .grade(alert.getGrade().name())
                .message(alert.getMessage())
                .isRead(alert.isRead())
                .createdAt(alert.getCreatedAt())
                .build();
    }

}
