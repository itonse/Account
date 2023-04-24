package com.example.account.repository;

import com.example.account.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository   // 빈으로 등록
public interface AccountRepository extends JpaRepository<Account, Long> {   //  Account 테이블에 접속하기 위한 인터페이스  <활용 엔티티, 엔티티의 PK 타입>
    // 스프링데이터 jpa에서 제공해주는 기능들을 사용해 DB에 간단하게 접근.

    Optional<Account> findFirstByOrderByIdDesc();  // ID를 내림차순 정렬해서 첫번째 값(가장 큰값)을 옵셔널 타입으로 가져옴
}

