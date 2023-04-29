package com.example.account.dto;

import com.example.account.type.ErrorCode;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorResponse {   // 에러응답 DTO
    private ErrorCode errorCode;
    private String errorMessage;
}
