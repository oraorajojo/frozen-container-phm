package com.singsing.frozenapi.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

// ERD의 branch 테이블과 매핑되는 JPA 엔티티
// users.branch_id, containers.branch_id 가 이 테이블의 branch_id를 참조(FK)하므로,
// Branch는 User/Container가 참조하는 "부모" 역할을 하는 마스터 데이터
@Entity
@Table(name = "branch")
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class) // 아래 @CreatedDate/@LastModifiedDate 자동 채움을 위해 필요
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "branch_id")
    private Integer branchId;

    @Column(nullable = false, length = 50) // 지점명
    private String name;

    @Column(length = 100) // 지점 주소, Null 허용(선택 입력)
    private String address;

    // 회의록 기준: 삭제 대신 상태 변경만 허용. 기본값 ACTIVE(운영중)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20, columnDefinition = "varchar(20) default 'ACTIVE'")
    private BranchStatus status;

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false, columnDefinition = "datetime default CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    // 지점명/주소/상태가 바뀔 때마다 자동으로 갱신됨 ("지점명 수정 시 로그 남기기" 요구사항을
    // 별도 이력 테이블 없이 간단하게 "마지막 수정 시각"으로 충족)
    // 참고: 최초 등록 시에도 @CreatedDate와 같은 시각으로 한 번 채워짐 (Auditing이 insert 시에도 동작하기 때문)
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ===== 수정용 메서드 =====
    public void changeName(String name) {
        this.name = name;
    }
    public void changeAddress(String address) {
        this.address = address;
    }

    // 상태 변경 전용 메서드 - 삭제 대신 이걸로만 지점을 비활성화한다 (BranchServiceImpl.changeStatus에서 호출)
    public void changeStatus(BranchStatus status) {
        this.status = status;
    }

}
