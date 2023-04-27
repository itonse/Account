package com.example.account.dto;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

public class DeleteAccount {
    @Getter
    @Setter
    @AllArgsConstructor
    public static class Request {  // 요청 이너클래스 (스태틱)
        @NotNull
        @Min(1)  // 1부터 시작
        private Long userId;  // 유저ID

        @NotBlank
        @Size(min = 10, max = 10)
        private String accountNumber; // 해지 대상인 계좌번호는-> 10자리의 공백이아닌 문자열
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {  // 응답 이너클래스
        private Long userId;  // 유저ID
        private String accountNumber;   // 계좌번호
        private LocalDateTime unregisteredAt;  // 등록일시

        public static Response from(AccountDto accountDto) {  // Response 객체 생성
        // accountDto -> CreateAccount Response로 쉽게 변환해주는 정적 메소드
            return Response.builder()
                    .userId(accountDto.getUserId())
                    .accountNumber(accountDto.getAccountNumber())
                    .unregisteredAt(accountDto.getUnRegisteredAt())
                    .build();
        }
    }

}

