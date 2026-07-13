package com.singsing.frozenapi.domain;

// 회원의 권한(등급)을 나타내는 enum
// DB에는 문자열("ADMIN", "STAFF")로 저장됨 (User.java에서 @Enumerated(EnumType.STRING) 사용)
// 이 시스템은 일반 사용자(고객) 회원가입이 아니라, 냉동 컨테이너를 관리하는 내부 인력(admin/staff)용 계정 구조
public enum Role {
    ADMIN, // 관리자
    STAFF; // 일반 직원 (회원가입 시 기본으로 부여되는 권한. admin은 셀프 가입으로 부여하지 않음)
}
