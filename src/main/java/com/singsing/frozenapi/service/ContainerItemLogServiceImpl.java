package com.singsing.frozenapi.service;

import com.singsing.frozenapi.domain.ContainerItemLog;
import com.singsing.frozenapi.dto.ContainerItemLogRequestDTO;
import com.singsing.frozenapi.dto.ContainerItemLogResponseDTO;
import com.singsing.frozenapi.repository.ContainerItemLogRepository;
import com.singsing.frozenapi.repository.ContainerRepository;
import com.singsing.frozenapi.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class ContainerItemLogServiceImpl implements ContainerItemLogService {

    private final ContainerItemLogRepository containerItemLogRepository;
    // container_id/item_id는 ContainerItemLog에 단순 값으로만 저장하지만(@ManyToOne 아님),
    // 존재하지 않는 컨테이너/품목으로 적재 등록하는 걸 막기 위해 두 리포지토리를 같이 주입받아 존재 여부를 검증한다.
    private final ContainerRepository containerRepository;
    private final ItemRepository itemRepository;

    @Override
    public ContainerItemLogResponseDTO register(ContainerItemLogRequestDTO containerItemLogRequestDTO) {
        log.info("*********** ContainerItemLogService - register: {}", containerItemLogRequestDTO);

        // FK 무결성 검증: DB에 실제로 존재하는 container_id/item_id인지 확인
        // (JPA @ManyToOne으로 강제하는 대신, 서비스 계층에서 직접 체크하는 이 프로젝트의 기존 방식과 동일)
        if (!containerRepository.existsById(containerItemLogRequestDTO.getContainerId())) {
            throw new NoSuchElementException("존재하지 않는 컨테이너입니다.");
        }
        if (!itemRepository.existsById(containerItemLogRequestDTO.getItemId())) {
            throw new NoSuchElementException("존재하지 않는 품목입니다.");
        }

        // loadedAt은 값을 넣지 않아도 저장 시점에 ContainerItemLog.java의 @CreatedDate가 자동으로 채워준다.
        ContainerItemLog containerItemLog = ContainerItemLog.builder()
                .containerId(containerItemLogRequestDTO.getContainerId())
                .itemId(containerItemLogRequestDTO.getItemId())
                .quantity(containerItemLogRequestDTO.getQuantity())
                .build();

        ContainerItemLog saved = containerItemLogRepository.save(containerItemLog);
        return entityToDTO(saved);
    }

    @Override
    public List<ContainerItemLogResponseDTO> getList() {
        return containerItemLogRepository.findAll().stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ContainerItemLogResponseDTO get(Integer logId) {
        return entityToDTO(getLogOrThrow(logId));
    }

    @Override
    public ContainerItemLogResponseDTO unload(Integer logId) {
        log.info("*********** ContainerItemLogService - unload - logId: {}", logId);

        ContainerItemLog containerItemLog = getLogOrThrow(logId);

        // 이미 출고 처리된(unloaded_at이 채워진) 로그를 또 출고 처리하려는 요청은 막는다
        if (containerItemLog.getUnloadedAt() != null) {
            throw new IllegalArgumentException("이미 출고 처리된 로그입니다.");
        }

        containerItemLog.markUnloaded(LocalDateTime.now());
        // 영속 상태 엔티티라 dirty checking으로 자동 UPDATE (save() 별도 호출 불필요)
        return entityToDTO(containerItemLog);
    }

    private ContainerItemLog getLogOrThrow(Integer logId) {
        return containerItemLogRepository.findById(logId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 적재 로그입니다."));
    }

    private ContainerItemLogResponseDTO entityToDTO(ContainerItemLog containerItemLog) {
        return ContainerItemLogResponseDTO.builder()
                .logId(containerItemLog.getLogId())
                .containerId(containerItemLog.getContainerId())
                .itemId(containerItemLog.getItemId())
                .quantity(containerItemLog.getQuantity())
                .loadedAt(containerItemLog.getLoadedAt())
                .unloadedAt(containerItemLog.getUnloadedAt())
                .build();
    }

}
