package com.example.account.service;

import com.example.account.domain.Account;
import com.example.account.domain.AccountStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest  // 환경을 테스트용으로 실제와 동일하게 모든 빈을 다 생성을 해서
// 그 빈들이 우리가 원하는 테스트를 할 수 있게 해줌.
class AccountServiceTest {   // 하위에 AccountRepository 의존성을 갖고있음.
    @Autowired  // 주입
    private AccountService accountService;

    @BeforeEach  // 사전에 각각 동작해서 먼저 데이터 저장하기
    void init() {   // 테스트를 하기 전에 동작시키고 테스르를 하고 이것을 동작시키고 다음 테스르를 함.
        accountService.createAccount();
    }

    @Test
    @DisplayName("Test 이름 변경")
    void testGetAccount() {  // Jnit 프레임워크가 실행시킴
        accountService.createAccount();  // Account 엔티티 하나를 생성하여 저장 함
        Account account = accountService.getAccount(1L);

        assertEquals("40000", account.getAccountNumber());
        assertEquals(AccountStatus.IN_USE, account.getAccountStatus());
    }

    @Test
    void testGetAccount2() {  // Jnit 프레임워크가 실행시킴
        accountService.createAccount();  // 하나를 저장 함
        Account account = accountService.getAccount(2L);

        assertEquals("40000", account.getAccountNumber());
        assertEquals(AccountStatus.IN_USE, account.getAccountStatus());
    }

}

