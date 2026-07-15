package com.singsing.frozenapi.repository;

import com.singsing.frozenapi.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Integer> {
}