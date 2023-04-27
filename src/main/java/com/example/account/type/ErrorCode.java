package com.example.account.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor  // 두개로 생성자를 자동으로 만듦
public enum ErrorCode {  // 커스텀 에러코드
    USER_NOT_FOUND("사용자가 없습니다."),
    ACCOUNT_NOT_FOUND("계좌가 없습니다."),
    USER_ACCOUNT_UN_MATCH("사용자와 계좌의 소유주가 다릅니다."),
    ACCOUNT_ALREADY_UNREGISTERED("계좌가 이미 해지되었습니다."),
    BALANCE_NOT_EMPTY("잔액이 있는 계좌는 해지할 수 없습니다."),
    MAX_ACCOUNT_PER_USER_10("사용자 최대 계좌는 10개 입니다.");


    private final String description; // enum 코드들의 정보들 넣기(이해하기 편함)
}
