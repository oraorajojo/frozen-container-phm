package com.singsing.frozenapi.domain;

// 회원의 권한(등급)을 나타내는 enum
// DB에는 문자열("USER", "ADMIN")로 저장됨 (User.java에서 @Enumerated(EnumType.STRING) 사용)
public enum Role {
    USER,  // 일반 회원 (회원가입 시 기본으로 부여되는 권한)
    ADMIN; // 관리자
}
