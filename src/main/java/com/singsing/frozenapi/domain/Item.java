package com.singsing.frozenapi.domain;

import jakarta.persistence.*;
import lombok.*;

// ERD의 items 테이블과 매핑되는 JPA 엔티티 클래스
// 컨테이너에 적재되는 품목(생선/육류 등)의 마스터 데이터
// container_item_log.item_id가 이 테이블의 item_id를 참조(FK)한다.
@Entity
@Table(name = "items")
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Integer itemId;

    // DB설계.pdf 기준: name varchar(50), NOT NULL
    @Column(nullable = false, length = 50) // 품목명 (예: 연어, 고등어, 참치)
    private String name;

    // DB설계.pdf 기준: food_type varchar(50), NOT NULL
    // 값 체계(한글 "육류/수산" vs 영어 "Seafood/Meat/Frozen")가 팀 내에서 아직 미확정이라
    // enum이 아니라 자유 문자열(String)로 두었다. 나중에 값 체계가 정해지면 enum으로 바꾸는 게 좋다.
    @Column(name = "food_type", nullable = false, length = 50) // 품목군 (예: 육류, 수산물 등 - 값 체계 미확정)
    private String foodType;

    // DB설계.pdf 기준: shelf_life_days int, NOT NULL
    @Column(name = "shelf_life_days", nullable = false) // 자체 유통기한 (일수)
    private Integer shelfLifeDays;

    // ===== 수정용 메서드 =====
    public void changeName(String name) {
        this.name = name;
    }
    public void changeFoodType(String foodType) {
        this.foodType = foodType;
    }
    public void changeShelfLifeDays(Integer shelfLifeDays) {
        this.shelfLifeDays = shelfLifeDays;
    }

}
