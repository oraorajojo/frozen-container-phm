package com.singsing.frozenapi.service;

import com.singsing.frozenapi.dto.ItemRequestDTO;
import com.singsing.frozenapi.dto.ItemResponseDTO;

import java.util.List;

// 품목 관련 비즈니스 로직의 규격만 정의. Container와 동일하게 마스터 데이터라 풀 CRUD로 구성
public interface ItemService {

    ItemResponseDTO register(ItemRequestDTO itemRequestDTO);                       // 등록 (Create)
    List<ItemResponseDTO> getList();                                               // 전체 목록 조회 (Read - all)
    ItemResponseDTO get(Integer itemId);                                           // 단건 조회 (Read - one)
    ItemResponseDTO modify(Integer itemId, ItemRequestDTO itemRequestDTO);         // 수정 (Update)
    void remove(Integer itemId);                                                   // 삭제 (Delete)

}
