package com.example.account.service;

import com.example.account.domain.Account;
import com.example.account.domain.AccountStatus;
import com.example.account.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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

        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);  // Long타입의 박스 생성

        //when
        Account account = accountService.getAccount(4555L);

        //then
         // 서비스를 테스트하는 다양한 방법들 (verify, assert 등)
        verify(accountRepository, times(1)).findById(captor.capture()); // 캡처가 값을 가로챔
         // accountRepository가 findById를 1회 들렸는지 검증
        assertEquals(4555L, captor.getValue()); // 캡처 값 검증
        assertNotEquals(6555L, captor.getValue()); // 캡처 값 검증
        verify(accountRepository, times(0)).save(any());
         // accountRepository가 save(any() 를 한 번도 호출 안했는지 검증
        assertEquals("65789", account.getAccountNumber());
        assertEquals(AccountStatus.UNREGISTERED, account.getAccountStatus());
    }

    @Test
    @DisplayName("계좌 조회 실패 - 음수로 조회")  // 로 표시됨
    void testFailedToSearchAccount() {
        //given
        //when
        RuntimeException exception = assertThrows(RuntimeException.class, () -> // () ->: 런타임 익셉션이 터지는 동작은
                accountService.getAccount(-10L));// 이 동작이다.

        //then
        assertEquals("Minus", exception.getMessage());
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

