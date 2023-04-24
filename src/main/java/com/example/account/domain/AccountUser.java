package com.example.account.domain;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
public class AccountUser {
    @Id  // PK 지정
    @GeneratedValue  // 자동으로 값 생성하도록
    private Long id;

    private String name;  // (특수데이터)사용자의 이름

    @CreatedDate  // 데이터를 자동으로 저장
    private LocalDateTime createdAt;  // 계좌 생성일
    @LastModifiedDate  // 데이터를 자동으로 바꿔줌 (저장)
    private LocalDateTime updatedAt;  // 계좌 수정일
}
