package com.singsing.frozenapi.service;

import com.singsing.frozenapi.domain.Branch;
import com.singsing.frozenapi.domain.BranchStatus;
import com.singsing.frozenapi.dto.BranchRequestDTO;
import com.singsing.frozenapi.dto.BranchResponseDTO;
import com.singsing.frozenapi.dto.BranchStatusUpdateRequestDTO;
import com.singsing.frozenapi.repository.BranchRepository;
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
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;

    @Override
    public BranchResponseDTO register(BranchRequestDTO branchRequestDTO) {
        log.info("*********** BranchService - register: {}", branchRequestDTO);

        // status는 Branch.java의 기본값(ACTIVE)으로 시작. 클라이언트가 정하지 않음
        Branch branch = Branch.builder()
                .name(branchRequestDTO.getName())
                .address(branchRequestDTO.getAddress())
                .status(BranchStatus.ACTIVE)
                .build();

        Branch saved = branchRepository.save(branch);
        return entityToDTO(saved);
    }

    @Override
    public List<BranchResponseDTO> getList() {
        return branchRepository.findAll().stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BranchResponseDTO get(Integer branchId) {
        return entityToDTO(getBranchOrThrow(branchId));
    }

    @Override
    public BranchResponseDTO modify(Integer branchId, BranchRequestDTO branchRequestDTO) {
        log.info("*********** BranchService - modify - branchId: {}, dto: {}", branchId, branchRequestDTO);

        Branch branch = getBranchOrThrow(branchId);
        // 영속 상태 엔티티 필드 변경 -> dirty checking으로 자동 UPDATE. updatedAt도 @LastModifiedDate가 자동 갱신
        branch.changeName(branchRequestDTO.getName());
        branch.changeAddress(branchRequestDTO.getAddress());

        return entityToDTO(branch);
    }

    @Override
    public BranchResponseDTO changeStatus(Integer branchId, BranchStatusUpdateRequestDTO statusUpdateRequestDTO) {
        log.info("*********** BranchService - changeStatus - branchId: {}, status: {}", branchId, statusUpdateRequestDTO.getStatus());

        Branch branch = getBranchOrThrow(branchId);
        branch.changeStatus(statusUpdateRequestDTO.getStatus());

        return entityToDTO(branch);
    }

    private Branch getBranchOrThrow(Integer branchId) {
        return branchRepository.findById(branchId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 지점입니다."));
    }

    private BranchResponseDTO entityToDTO(Branch branch) {
        return BranchResponseDTO.builder()
                .branchId(branch.getBranchId())
                .name(branch.getName())
                .address(branch.getAddress())
                .status(branch.getStatus().name())
                .createdAt(branch.getCreatedAt())
                .updatedAt(branch.getUpdatedAt())
                .build();
    }

}
