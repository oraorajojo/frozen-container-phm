package com.singsing.frozenapi.repository;

import com.singsing.frozenapi.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

// User 엔티티에 대한 DB 접근(CRUD)을 담당하는 리포지토리 인터페이스
// JpaRepository<User, Integer> 을 상속받는 것만으로 save(), findById(), findAll(), delete() 등이 자동 구현됨
// (Integer는 User 엔티티의 PK 타입인 userId의 타입 - DB설계.pdf 기준 user_id가 int라서 Long 대신 Integer 사용)
public interface UserRepository extends JpaRepository<User, Integer> {

    // Spring Data JPA의 "쿼리 메서드" 기능:
    // 메서드 이름을 existsBy + 필드명(Email) 규칙대로 지으면,
    // 별도의 구현 코드 없이 Spring이 자동으로
    // "select case when count(u)>0 then true else false end from User u where u.email = ?" 쿼리를 만들어 실행해준다.
    // -> 회원가입 시 이메일 중복 여부 체크에 사용 (UserServiceImpl.signup 참고)
    boolean existsByEmail(String email);

}
