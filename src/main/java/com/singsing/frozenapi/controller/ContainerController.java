package com.singsing.frozenapi.controller;

import com.singsing.frozenapi.dto.ContainerRequestDTO;
import com.singsing.frozenapi.dto.ContainerResponseDTO;
import com.singsing.frozenapi.service.ContainerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// 컨테이너(Container) 관련 HTTP 요청을 받는 컨트롤러
// 클라이언트(리액트 등) <-> 서버 사이의 진입점 역할만 하고, 실제 로직은 ContainerService에 위임한다.
// UserController와 달리 등록뿐 아니라 조회/수정/삭제까지 풀 CRUD를 제공한다.
@RestController                    // @Controller + @ResponseBody. 리턴값을 뷰(HTML)가 아니라 JSON으로 바로 응답
@RequestMapping("/api/containers") // 이 컨트롤러의 모든 API는 "/api/containers"로 시작
@Slf4j
@RequiredArgsConstructor           // final 필드(containerService)를 생성자로 주입받음
public class ContainerController {

    private final ContainerService containerService;

    // 컨테이너 등록 : POST /api/containers
    // Body(JSON): { "modelName": "FRZ-2000", "installLocation": "냉동창고 A동 1구역" }
    @PostMapping
    public ResponseEntity<ContainerResponseDTO> register(@Valid @RequestBody ContainerRequestDTO containerRequestDTO) {
        // @RequestBody : 요청 JSON body를 ContainerRequestDTO로 자동 변환
        // @Valid       : @NotBlank 등 검증 어노테이션 실행, 실패 시 CustomControllerAdvice가 400 응답 처리
        log.info("*********** ContainerController - register: {}", containerRequestDTO);
        ContainerResponseDTO responseDTO = containerService.register(containerRequestDTO);
        // 자원이 새로 생성되었으므로 201 Created + 생성된 컨테이너 정보 응답 (UserController.signup과 동일한 관례)
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    // 전체 목록 조회 : GET /api/containers
    @GetMapping
    public ResponseEntity<List<ContainerResponseDTO>> list() {
        return ResponseEntity.ok(containerService.getList());
    }

    // 단건 조회 : GET /api/containers/{containerId}
    @GetMapping("/{containerId}")
    public ResponseEntity<ContainerResponseDTO> get(@PathVariable Integer containerId) {
        // @PathVariable : URL 경로의 {containerId} 부분을 메서드 파라미터로 그대로 받음
        // 존재하지 않는 id면 ContainerService 내부에서 NoSuchElementException이 발생 -> 404 응답으로 자동 변환됨
        return ResponseEntity.ok(containerService.get(containerId));
    }

    // 수정 : PUT /api/containers/{containerId}
    @PutMapping("/{containerId}")
    public ResponseEntity<ContainerResponseDTO> modify(
            @PathVariable Integer containerId,
            @Valid @RequestBody ContainerRequestDTO containerRequestDTO) {
        log.info("*********** ContainerController - modify - containerId: {}", containerId);
        return ResponseEntity.ok(containerService.modify(containerId, containerRequestDTO));
    }

    // 삭제 : DELETE /api/containers/{containerId}
    @DeleteMapping("/{containerId}")
    public ResponseEntity<Map<String, Boolean>> remove(@PathVariable Integer containerId) {
        log.info("*********** ContainerController - remove - containerId: {}", containerId);
        containerService.remove(containerId);
        // 삭제 성공 여부만 알려주면 되므로 간단히 { "result": true } 형태로 응답
        return ResponseEntity.ok(Map.of("result", true));
    }

}
