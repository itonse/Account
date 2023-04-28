package com.example.account.dto;

import com.example.account.type.TransactionResultType;
import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

public class UseBalance {
    /**
     * {
     *  "userId":1,
     *  "accountNumber":"1000000000",
     *  "amount":1000
     * }
     */
    @Getter
    @Setter
    @AllArgsConstructor
    public static class Request {  // 요청 이너클래스 (스태틱)
        @NotNull
        @Min(1)  // 1부터 시작
        private Long userId;  // 유저ID

        @NotBlank
        @Size(min = 10, max = 10)
        private String accountNumber;

        @NotNull
        @Min(10)  // 최소 거래금액
        @Max(1000_000_000)   // 최대 거래금액 (10억)
        private Long amount;
    }

    /**
     * {
     *  "accountNumber":"1234567890",
     *  "transactionResult":"S",
     *  "transactionId":"c2033bb6d82a4250aecf8e27c49b63f6",
     *  "amount":1000,
     *  "transactedAt":"2022-06-01T23:26:14.671859"
     * }
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private String accountNumber;
        private TransactionResultType transactionResult;   // 성공,실패
        private String transactionId;
        private Long amount;
        private LocalDateTime transactedAt;

        public static Response from(TransactionDto transactionDto) {
            return Response.builder()
                    .accountNumber(transactionDto.getAccontNumber())
                    .transactionResult(transactionDto.getTransactionResultType())
                    .transactionId(transactionDto.getTransactionId())
                    .amount(transactionDto.getAmount())
                    .transactedAt(transactionDto.getTransactedAt())
                    .build();
        }
    }

}
