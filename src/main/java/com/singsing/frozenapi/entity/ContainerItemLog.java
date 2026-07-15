package com.singsing.frozenapi.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;

@Entity
@Table(name = "container_item_log")
@Data
public class ContainerItemLog {
    @Id
    @Column(name = "log_id")
    private Integer logId;

    @Column(name = "container_id")
    private Integer containerId;

    @Column(name = "item_id")
    private Integer itemId;

    private java.math.BigDecimal quantity;

    @Column(name = "loaded_at")
    private LocalDateTime loadedAt;

    @Column(name = "unloaded_at")
    private LocalDateTime unloadedAt;
}