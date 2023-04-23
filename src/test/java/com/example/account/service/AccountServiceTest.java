package com.example.account.service;

import com.example.account.domain.Account;
import com.example.account.domain.AccountStatus;
import com.example.account.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)   // Mockto: DB의 데이터나 의존관계 바뀜 등 상관없이 온전이 맏은 역할만 테스트 함.
class AccountServiceTest {   // 하위에 AccountRepository 의존성을 갖고있음.

    @Mock
    private AccountRepository accountRepository;
    // 가짜로 accountRepository을 생성하여 Mockito의 Mock으로 만듦

    @InjectMocks
    private AccountService accountService;  // accountRepository를 accountService에 인젝트

    @Test
    @DisplayName("계좌 조회 성공")  // 로 표시됨
    void testXXX() {
        //given
        given(accountRepository.findById(anyLong()))  // 목
                .willReturn(Optional.of(Account.builder()
                        .accountStatus(AccountStatus.UNREGISTERED)
                        .accountNumber("65789")
                        .build()));

        //when
        Account account = accountService.getAccount(4555L);

        //then
        assertEquals("65789", account.getAccountNumber());
        assertEquals(AccountStatus.UNREGISTERED, account.getAccountStatus());
    }

    @BeforeEach  // 사전에 각각 동작해서 먼저 데이터 저장하기
    void init() {   // 테스트를 하기 전에 동작시키고 테스르를 하고 이것을 동작시키고 다음 테스르를 함.
        accountService.createAccount();
    }

    @Test
    @DisplayName("Test 이름 변경")
    void testGetAccount() {  // Jnit 프레임워크가 실행시킴
        //given
        given(accountRepository.findById(anyLong()))  // 목
                .willReturn(Optional.of(Account.builder()
                        .accountStatus(AccountStatus.UNREGISTERED)
                        .accountNumber("65789")
                        .build()));

        //when
        Account account = accountService.getAccount(4555L);

        //then
        assertEquals("65789", account.getAccountNumber());
        assertEquals(AccountStatus.UNREGISTERED, account.getAccountStatus());
    }

    @Test
    void testGetAccount2() {  // Jnit 프레임워크가 실행시킴
        //given
        given(accountRepository.findById(anyLong()))  // 목
                .willReturn(Optional.of(Account.builder()
                        .accountStatus(AccountStatus.UNREGISTERED)
                        .accountNumber("65789")
                        .build()));

        //when
        Account account = accountService.getAccount(4555L);

        //then
        assertEquals("65789", account.getAccountNumber());
        assertEquals(AccountStatus.UNREGISTERED, account.getAccountStatus());
    }

}

