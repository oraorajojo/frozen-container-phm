package com.singsing.frozenapi.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

// ERD의 alerts 테이블과 매핑되는 JPA 엔티티
// 컴프레서 이상, 유통기한 임박 등 컨테이너에서 발생한 알림(이벤트)을 기록하는 테이블
@Entity
@Table(name = "alerts")
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class) // created_at을 @CreatedDate로 자동 채우기 위해 필요
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alert_id")
    private Integer alertId;

    @Column(name = "container_id", nullable = false) // FK -> containers.container_id (ContainerItemLog와 동일하게 단순 값 저장)
    private Integer containerId;

    // DB설계.pdf 기준: source varchar(20), 값은 compressor/shelf_life
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AlertSource source;

    // DB설계.pdf 기준: grade varchar(20), 값은 Green/Yellow/Red
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AlertGrade grade;

    @Column(length = 255) // Null 허용. 알림 상세 메시지
    private String message;

    // DB설계.pdf 기준: is_read boolean, 기본값 false
    @Column(name = "is_read", nullable = false)
    @Builder.Default // register()에서 값을 안 넣으면 항상 false로 시작하도록 강제 (읽음 여부를 클라이언트가 처음부터 true로 등록하면 안 되므로)
    private boolean isRead = false;

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false, columnDefinition = "datetime default CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    // ===== 수정용 메서드 =====
    // 읽음 처리 : AlertServiceImpl.markAsRead에서 호출
    public void markAsRead() {
        this.isRead = true;
    }

}
