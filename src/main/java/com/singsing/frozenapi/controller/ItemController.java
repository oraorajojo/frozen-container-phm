package com.singsing.frozenapi.controller;

import com.singsing.frozenapi.dto.ItemRequestDTO;
import com.singsing.frozenapi.dto.ItemResponseDTO;
import com.singsing.frozenapi.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// 품목(Item) 관련 REST API. ContainerController와 동일한 패턴으로 /api/items 하위에 풀 CRUD 제공
@RestController
@RequestMapping("/api/items")
@Slf4j
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    // 등록 : POST /api/items
    @PostMapping
    public ResponseEntity<ItemResponseDTO> register(@Valid @RequestBody ItemRequestDTO itemRequestDTO) {
        log.info("*********** ItemController - register: {}", itemRequestDTO);
        ItemResponseDTO responseDTO = itemService.register(itemRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    // 전체 목록 조회 : GET /api/items
    @GetMapping
    public ResponseEntity<List<ItemResponseDTO>> list() {
        return ResponseEntity.ok(itemService.getList());
    }

    // 단건 조회 : GET /api/items/{itemId}
    @GetMapping("/{itemId}")
    public ResponseEntity<ItemResponseDTO> get(@PathVariable Integer itemId) {
        return ResponseEntity.ok(itemService.get(itemId));
    }

    // 수정 : PUT /api/items/{itemId}
    @PutMapping("/{itemId}")
    public ResponseEntity<ItemResponseDTO> modify(
            @PathVariable Integer itemId,
            @Valid @RequestBody ItemRequestDTO itemRequestDTO) {
        log.info("*********** ItemController - modify - itemId: {}", itemId);
        return ResponseEntity.ok(itemService.modify(itemId, itemRequestDTO));
    }

    // 삭제 : DELETE /api/items/{itemId}
    @DeleteMapping("/{itemId}")
    public ResponseEntity<Map<String, Boolean>> remove(@PathVariable Integer itemId) {
        log.info("*********** ItemController - remove - itemId: {}", itemId);
        itemService.remove(itemId);
        return ResponseEntity.ok(Map.of("result", true));
    }

}
