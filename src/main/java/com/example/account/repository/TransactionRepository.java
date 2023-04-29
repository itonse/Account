package com.example.account.repository;

import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import com.example.account.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository   // 빈으로 등록
public interface TransactionRepository extends JpaRepository<Transaction, Long> {  // Transaction 사용
    // 스프링데이터 JPA에서 제공해주는 기능들을 사용해 DB에 간단하게 접근.
    // 인터페이스를 상속받아서, 구현체는 직접 만들지 않음.
    Optional<Transaction> findByTransactionId(String transactionId);
        // TransactionId 컬럼을 통해서 SELECT를 해주는 쿼리가 자동으로 생성이 됨.
}

