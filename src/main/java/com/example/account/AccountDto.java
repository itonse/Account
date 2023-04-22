package com.example.account;

import lombok.*;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor  // 모든 property를 다 가지고 있는 생성자를 만들어줌
@RequiredArgsConstructor  // 필수 값들을 받게하는 생성자들을 만들어줌, (쓰임: 자동으로 자바빈을 주입받음)
//@Data  // 너무 강력하지만 보안 문제 등으로 잘 안씀. 필요한거 하나하나 추가하는게 좋음
@Slf4j
public class AccountDto {
    // 자바빈 규약에는 필드는 직접 접근하지 말고, getter&setter를 이용해서 접근하라는 규약이 있음.
    // -> 속성들은 모두 private로 놓기.
    private String accountNumber;   // 계좌번호
    private String nickname;  // 별명
    private LocalDateTime registeredAt;   // 계좌가 등록된 시간

    public void log() {  // 클래스 정보를 담음
        log.error("error is occurred.");  // by @Slf4j
    }
}
