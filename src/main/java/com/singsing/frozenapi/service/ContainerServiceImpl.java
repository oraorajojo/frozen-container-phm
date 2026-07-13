package com.singsing.frozenapi.service;

import com.singsing.frozenapi.domain.Container;
import com.singsing.frozenapi.dto.ContainerRequestDTO;
import com.singsing.frozenapi.dto.ContainerResponseDTO;
import com.singsing.frozenapi.repository.ContainerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

// ContainerService 인터페이스의 실제 구현체. 컨테이너 CRUD의 핵심 로직이 들어있는 곳
@Service             // 이 클래스를 Spring이 관리하는 Bean으로 등록 (컨트롤러 등에서 자동 주입 가능해짐)
@Transactional        // 이 클래스의 메서드 실행 중 예외가 발생하면 DB 변경사항을 자동 롤백해주는 트랜잭션 처리
@Slf4j                 // log.info(), log.error() 등을 바로 쓸 수 있게 해주는 로깅 어노테이션
@RequiredArgsConstructor // final 필드(containerRepository)를 파라미터로 받는 생성자를 자동 생성 -> Spring이 이 생성자로 의존성 주입(DI)
public class ContainerServiceImpl implements ContainerService {

    private final ContainerRepository containerRepository;

    @Override
    public ContainerResponseDTO register(ContainerRequestDTO containerRequestDTO) {
        log.info("*********** ContainerService - register: {}", containerRequestDTO);

        // registeredAt은 여기서 값을 넣지 않아도, DB 저장(save) 시점에
        // Container.java의 @CreatedDate가 오늘 날짜로 자동 채워준다.
        Container container = Container.builder()
                .modelName(containerRequestDTO.getModelName())
                .installLocation(containerRequestDTO.getInstallLocation())
                .build();

        Container saved = containerRepository.save(container);
        return entityToDTO(saved);
    }

    @Override
    public List<ContainerResponseDTO> getList() {
        // findAll()로 containers 테이블의 모든 row를 가져온 뒤,
        // 스트림으로 각 Container 엔티티를 ContainerResponseDTO로 하나씩 변환해서 리스트로 모음
        return containerRepository.findAll().stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ContainerResponseDTO get(Integer containerId) {
        Container container = getContainerOrThrow(containerId);
        return entityToDTO(container);
    }

    @Override
    public ContainerResponseDTO modify(Integer containerId, ContainerRequestDTO containerRequestDTO) {
        log.info("*********** ContainerService - modify - containerId: {}, dto: {}", containerId, containerRequestDTO);

        // 먼저 존재하는 컨테이너인지 조회 (없으면 아래 getContainerOrThrow에서 예외 발생)
        Container container = getContainerOrThrow(containerId);

        // 변경 메서드(Container.changeXxx)로 값을 수정
        // 여기서 중요한 점: @Transactional 메서드 안에서 조회한 엔티티(container)는 "영속 상태"라서
        // 필드 값을 바꾸기만 해도, 메서드가 끝나고 트랜잭션이 커밋될 때
        // JPA가 변경사항을 자동 감지(dirty checking)해서 알아서 UPDATE 쿼리를 실행해준다.
        // -> 따라서 containerRepository.save(container)를 별도로 호출하지 않아도 된다.
        container.changeModelName(containerRequestDTO.getModelName());
        container.changeInstallLocation(containerRequestDTO.getInstallLocation());

        return entityToDTO(container);
    }

    @Override
    public void remove(Integer containerId) {
        log.info("*********** ContainerService - remove - containerId: {}", containerId);

        // 존재하지 않는 id를 삭제하려고 하면 (아무 일도 없었다는 듯 조용히 넘어가지 않고)
        // 명확하게 예외를 던져서 클라이언트에게 "그런 컨테이너 없다"고 알려준다.
        if (!containerRepository.existsById(containerId)) {
            throw new NoSuchElementException("존재하지 않는 컨테이너입니다.");
        }
        containerRepository.deleteById(containerId);
    }

    // id로 컨테이너를 조회하되, 없으면 예외를 던지는 공통 로직을 메서드로 분리
    // -> get()과 modify()에서 똑같이 반복되는 "조회 후 없으면 예외" 패턴을 한 곳에 모아 중복 제거
    // 여기서 던지는 NoSuchElementException은 CustomControllerAdvice.handleNotFound()가 잡아서 404 응답으로 변환한다.
    private Container getContainerOrThrow(Integer containerId) {
        return containerRepository.findById(containerId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 컨테이너입니다."));
    }

    // Container 엔티티 -> ContainerResponseDTO 변환 전용 private 메서드
    private ContainerResponseDTO entityToDTO(Container container) {
        return ContainerResponseDTO.builder()
                .containerId(container.getContainerId())
                .modelName(container.getModelName())
                .installLocation(container.getInstallLocation())
                .registeredAt(container.getRegisteredAt())
                .build();
    }

}
