package com.example.account.exception;

import com.example.account.type.ErrorCode;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountException extends RuntimeException{
    private ErrorCode errorCode;  // 커스텀 에러코드
    private String errorMessage;  // 에러타입 별로 클래스를 생성하는 번거로움 필요없이 커스텀 익셉션을 만든다.

    public AccountException(ErrorCode errorCode) {  // 이 생성자로 더 깔끔하게 익셉션 생성
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }
}
