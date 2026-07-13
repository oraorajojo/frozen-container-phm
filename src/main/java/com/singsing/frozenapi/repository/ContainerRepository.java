package com.singsing.frozenapi.repository;

import com.singsing.frozenapi.domain.Container;
import org.springframework.data.jpa.repository.JpaRepository;

// Container 엔티티에 대한 DB 접근(CRUD)을 담당하는 리포지토리 인터페이스
// JpaRepository<Container, Integer> 을 상속받는 것만으로
// save(), findById(), findAll(), existsById(), deleteById() 등이 별도 구현 없이 자동으로 제공된다.
// (Integer는 Container 엔티티의 PK 타입인 containerId의 타입 - DB설계.pdf 기준 container_id가 int라서 Long 대신 Integer 사용)
//
// 지금은 Container만의 특별한 조회 조건이 없어서 UserRepository의 existsByEmail() 같은
// 커스텀 쿼리 메서드 없이 JpaRepository 기본 기능만으로 충분하다.
public interface ContainerRepository extends JpaRepository<Container, Integer> {
}
