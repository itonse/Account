package com.example.account.domain;

import com.example.account.type.AccountStatus;
import com.example.account.type.TransactionResultType;
import com.example.account.type.TransactionType;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Transaction extends BaseEntity{   // TransactionRepository에 사용
    // TransactionController -> TransactionService -> Transaction 구조

    @Enumerated(EnumType.STRING)   // enum 타입을 1,2 등 숫자가 아닌 문자열로 저장
    private TransactionType transactionType;
    @Enumerated(EnumType.STRING)
    private TransactionResultType transactionResultType;

    @ManyToOne   // Transacion N개가 특정 Account 하나에 연결 되도록.
    private Account account;
    private Long amount; // 거래 금액
    private Long balanceSnapshot;  // 거래 후 계좌 잔액

    private String transactionId;  // 보안상의 이유로 위에 id를 노출시키지 않고, 이것을 쓴다
    private LocalDateTime transactedAt;   // 거래시간의 스냅샷 용도

}
