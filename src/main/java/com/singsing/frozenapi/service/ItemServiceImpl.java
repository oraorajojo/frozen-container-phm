package com.singsing.frozenapi.service;

import com.singsing.frozenapi.domain.Item;
import com.singsing.frozenapi.dto.ItemRequestDTO;
import com.singsing.frozenapi.dto.ItemResponseDTO;
import com.singsing.frozenapi.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

// ItemService 구현체. ContainerServiceImpl과 거의 동일한 구조(마스터 데이터 풀 CRUD)
@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    @Override
    public ItemResponseDTO register(ItemRequestDTO itemRequestDTO) {
        log.info("*********** ItemService - register: {}", itemRequestDTO);

        Item item = Item.builder()
                .name(itemRequestDTO.getName())
                .foodType(itemRequestDTO.getFoodType())
                .shelfLifeDays(itemRequestDTO.getShelfLifeDays())
                .build();

        Item saved = itemRepository.save(item);
        return entityToDTO(saved);
    }

    @Override
    public List<ItemResponseDTO> getList() {
        return itemRepository.findAll().stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ItemResponseDTO get(Integer itemId) {
        return entityToDTO(getItemOrThrow(itemId));
    }

    @Override
    public ItemResponseDTO modify(Integer itemId, ItemRequestDTO itemRequestDTO) {
        log.info("*********** ItemService - modify - itemId: {}, dto: {}", itemId, itemRequestDTO);

        Item item = getItemOrThrow(itemId);
        // 영속 상태 엔티티의 필드 변경 -> 트랜잭션 커밋 시 dirty checking으로 자동 UPDATE (Container와 동일한 방식)
        item.changeName(itemRequestDTO.getName());
        item.changeFoodType(itemRequestDTO.getFoodType());
        item.changeShelfLifeDays(itemRequestDTO.getShelfLifeDays());

        return entityToDTO(item);
    }

    @Override
    public void remove(Integer itemId) {
        log.info("*********** ItemService - remove - itemId: {}", itemId);
        if (!itemRepository.existsById(itemId)) {
            throw new NoSuchElementException("존재하지 않는 품목입니다.");
        }
        itemRepository.deleteById(itemId);
    }

    private Item getItemOrThrow(Integer itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 품목입니다."));
    }

    private ItemResponseDTO entityToDTO(Item item) {
        return ItemResponseDTO.builder()
                .itemId(item.getItemId())
                .name(item.getName())
                .foodType(item.getFoodType())
                .shelfLifeDays(item.getShelfLifeDays())
                .build();
    }

}
