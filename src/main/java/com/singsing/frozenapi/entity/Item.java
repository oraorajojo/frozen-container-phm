package com.singsing.frozenapi.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "items")
@Data
public class Item {
    @Id
    @Column(name = "item_id")
    private Integer itemId;

    private String name;

    @Column(name = "food_type")
    private String foodType;

    @Column(name = "shelf_life_days")
    private Integer shelfLifeDays;
}