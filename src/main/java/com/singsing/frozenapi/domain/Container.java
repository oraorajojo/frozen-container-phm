package com.singsing.frozenapi.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

// ERD의 containers 테이블과 매핑되는 JPA 엔티티 클래스
// 이 클래스의 인스턴스 하나가 DB의 containers 테이블 row 하나에 대응된다.
//
// container_item_log.container_id, alerts.container_id 가 이 테이블의 container_id를 참조(FK)하므로,
// Container는 다른 엔티티들이 참조하는 "부모" 역할을 하는 마스터 데이터
@Entity                      // 이 클래스가 JPA 엔티티임을 선언 (테이블과 매핑됨)
@Table(name = "containers")  // 매핑될 실제 테이블 이름 지정 (클래스명은 Container지만 테이블명은 containers)
@Getter                      // 모든 필드에 대해 getter 메서드를 자동 생성 (예: getContainerId(), getModelName())
@ToString                    // 로그 출력 등에 사용할 toString() 자동 생성
@Builder                     // Container.builder().modelName("...")... 형태로 객체 생성 가능하게 해줌
@AllArgsConstructor          // 모든 필드를 파라미터로 받는 생성자 자동 생성 (Builder가 내부적으로 사용)
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 파라미터 없는 기본 생성자 (JPA 필수 요구사항). 외부에서 함부로 못 쓰게 protected로 제한
@EntityListeners(AuditingEntityListener.class) // 아래 @CreatedDate 필드를 자동으로 채워주는 리스너 등록 (User.java와 동일한 방식)
public class Container {

    @Id // 이 필드가 테이블의 기본키(PK)임을 표시
    @GeneratedValue(strategy = GenerationType.IDENTITY) // PK 값을 DB(AUTO_INCREMENT)가 자동으로 채번하도록 위임
    @Column(name = "container_id") // 실제 DB 컬럼명 지정 (Java 필드명 containerId -> DB 컬럼명 container_id)
    private Integer containerId;

    // DB설계.pdf 기준: model_name varchar(50), NOT NULL
    @Column(name = "model_name", nullable = false, length = 50) // 컨테이너/컴프레서 모델명 (예: "FRZ-2000")
    private String modelName;

    // DB설계.pdf 기준: install_location varchar(100), Null 허용(Y), 기본값 NULL
    // -> model_name과 달리 설치 위치는 아직 모를 수 있어 선택 입력 항목으로 설계됨
    @Column(name = "install_location", length = 100) // nullable 기본값 true라 별도 지정 안 함
    private String installLocation;

    // ERD상 registered_at은 datetime이 아니라 date 타입이라 LocalDateTime이 아닌 LocalDate 사용
    // (User.createdAt은 LocalDateTime이었던 것과의 차이점)
    @CreatedDate // 엔티티가 처음 저장(insert)될 때 현재 날짜가 자동으로 채워짐 (수정 시에는 값이 바뀌지 않음)
    @Column(name = "registered_at", updatable = false, nullable = false) // updatable = false : 이후 update 쿼리에서는 이 컬럼을 건드리지 않음
    private LocalDate registeredAt;

    // ===== 수정용 메서드 =====
    // JPA 엔티티는 Setter를 열어두지 않고, "의미가 드러나는 메서드"로만 값을 바꾸는 것이 관례
    // ContainerServiceImpl.modify()에서 이 메서드들을 호출해서 값을 바꾼다.

    public void changeModelName(String modelName) {
        this.modelName = modelName;
    }
    public void changeInstallLocation(String installLocation) {
        this.installLocation = installLocation;
    }

}
