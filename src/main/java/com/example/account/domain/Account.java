package com.example.account.domain;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder // 객체 생성 (위의 NAC와 AAC 필요)
@Entity  // 일종의 설정 클래스(자바 객체처럼 보이지만 사실은 설정)
public class Account {   // Account 테이블 구조
    @Id
    @GeneratedValue
    private Long id;   // Account 테이블에 id란 이름으로 PrimaryKey 지정

    private  String accountNumber;   // 계좌번호

    @Enumerated(EnumType.STRING)   // EnumType.STRING: Enum값에 숫자 0,1,2이 아닌, Enum값의 실제 문자열이 DB에 저장됨.
    private  AccountStatus accountStatus;
}
