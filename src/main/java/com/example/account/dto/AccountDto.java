package com.example.account.dto;

import com.example.account.domain.Account;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDto {  // Account 엔티티 클래스와 거의 비슷한데 더 단순화된 버전으로, 필요한것만 넣음
    // AccountController와 AccountService  간의 통신(데이터를 주고받음)을 할 때 쓰임, 목적 분명
    private Long userId;
    private String accountNumber;
    private Long balance;

    private LocalDateTime registeredAt;
    private LocalDateTime unRegisteredAt;

    public static AccountDto fromEntity(Account account) {  // 특정 엔티티에서 특정 DTO로 변환해주는 정적 메소드 (가독성, 안전)
        return AccountDto.builder()
                .userId(account.getAccountUser().getId())
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .registeredAt(account.getRegisteredAt())
                .unRegisteredAt(account.getUnRegisteredAt())
                .build();
    }
}
