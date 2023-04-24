package com.example.account.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;

public class CreateAccount {
    @Getter
    @Setter
    @AllArgsConstructor
    public static class Request {  // 요청 이너클래스 (스태틱)
        @NotNull(message = " userId is NUll!! ")
        @Min(1)  // 1부터 시작
        private Long userId;  // 유저ID

        @NotNull
        @Min(100)  // 최소 값은 100원 이상이여야 함
        private Long initialBalance;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {  // 응답 이너클래스
        private Long userId;  // 유저ID
        private String accountNumber;   // 계좌번호
        private LocalDateTime registeredAt;  // 등록일시

        public static Response from(AccountDto accountDto) {  // Response 객체 생성
        // accountDto -> CreateAccount Response로 쉽게 변환해주는 정적 메소드
            return Response.builder()
                    .userId(accountDto.getUserId())
                    .accountNumber(accountDto.getAccountNumber())
                    .registeredAt(accountDto.getRegisteredAt())
                    .build();
        }
    }

}

