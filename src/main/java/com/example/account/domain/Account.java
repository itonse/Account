package com.example.account.domain;

import com.example.account.type.AccountStatus;
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
@Builder // 객체 생성 (위의 NAC와 AAC 필요)
@Entity  // 일종의 설정 클래스(자바 객체처럼 보이지만 사실은 설정)
@EntityListeners(AuditingEntityListener.class)
public class Account {   // Account 테이블 구조
    @Id
    @GeneratedValue
    private Long id;   // Account 테이블에 id란 이름으로 PrimaryKey 지정

    @ManyToOne
    private AccountUser accountUser;   // Account 안에는 하위로 AccountUser가 있음 (AccountUser 한 명이 N개의 Account 가질수 있음
    private String accountNumber;

    @Enumerated(EnumType.STRING)  // enum이 0,1,2.. 순차적인 아닌 스트링으로 DB 테이블에 저장
    private AccountStatus accountStatus;
    private Long balance;

    private LocalDateTime registeredAt;
    private LocalDateTime unRegisteredAt;

    @CreatedDate  // 데이터를 자동으로 저장
    private LocalDateTime createdAt;
    @LastModifiedDate  // 데이터를 자동으로 바꿔줌 (저장)
    private LocalDateTime updatedAt;
}
