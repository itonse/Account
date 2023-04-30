package com.example.account.domain;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)  // 자동으로 생성시간, 업데이트시간을 현재시간으로 담아줌
public class BaseEntity {
    @Id
    @GeneratedValue  // 자동으로 값 생성하도록 (1, 2, 3, ...)
    private Long id;    // Account 테이블에 id란 이름으로 PrimaryKey 지정

    @CreatedDate  // 데이터를 자동으로 저장
    private LocalDateTime createdAt;  // 계좌 생성일
    @LastModifiedDate  // 데이터를 자동으로 바꿔줌 (저장)
    private LocalDateTime updatedAt;  // 계좌 수정일
}
