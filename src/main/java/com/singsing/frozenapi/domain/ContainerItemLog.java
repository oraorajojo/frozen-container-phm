package com.singsing.frozenapi.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// ERD의 container_item_log 테이블과 매핑되는 JPA 엔티티
// "어떤 컨테이너에 어떤 품목이 언제 실렸다가 언제 나갔는지"를 기록하는 로그(이력) 테이블
//
// containers, items를 각각 FK로 참조하지만, 이 프로젝트의 다른 엔티티들과 동일하게
// @ManyToOne 객체 연관관계를 맺지 않고 컨테이너/품목의 id 값만 그대로 저장하는 방식(단순 FK 컬럼)을 쓴다.
// (참조 무결성 검증은 ContainerItemLogServiceImpl에서 존재 여부를 직접 확인하는 방식으로 처리)
@Entity
@Table(name = "container_item_log")
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class) // loaded_at을 @CreatedDate로 자동 채우기 위해 필요
public class ContainerItemLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Integer logId;

    @Column(name = "container_id", nullable = false) // FK -> containers.container_id
    private Integer containerId;

    @Column(name = "item_id", nullable = false) // FK -> items.item_id
    private Integer itemId;

    // DB설계.pdf 기준: decimal(10,2), Null 허용
    @Column(precision = 10, scale = 2) // 적재 수량(kg). nullable 기본값 true라 별도 지정 안 함
    private BigDecimal quantity;

    // 이 row가 생성되는 시점 = 실제로 품목이 적재된 시점이라고 보고 @CreatedDate로 자동 채움
    @CreatedDate
    @Column(name = "loaded_at", updatable = false, nullable = false)
    private LocalDateTime loadedAt;

    // 출고 전에는 NULL. 출고 처리(unload) 되는 시점에만 값이 채워짐
    // -> "unloaded_at이 NULL이면 현재 실려있는 중"이라는 의미로 사용됨 (ContainerItemLogRepository 참고)
    @Column(name = "unloaded_at")
    private LocalDateTime unloadedAt;

    // ===== 수정용 메서드 =====
    // 출고 처리 : unloadedAt을 채운다 (ContainerItemLogServiceImpl.unload에서 호출)
    public void markUnloaded(LocalDateTime unloadedAt) {
        this.unloadedAt = unloadedAt;
    }

}
