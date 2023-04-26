package com.example.account.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor  // 두개로 생성자를 자동으로 만듦
public enum ErrorCode {  // 커스텀 에러코드
    USER_NOT_FOUND("사용자가 없습니다."),
    MAX_ACCOUNT_PER_USER_10("사용자 최대 계좌는 10개 입니다.");

    private final String description; // enum 코드들의 정보들 넣기(이해하기 편함)
}
