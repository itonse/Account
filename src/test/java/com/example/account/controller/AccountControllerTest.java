package com.example.account.controller;

import com.example.account.domain.Account;
import com.example.account.domain.AccountStatus;
import com.example.account.service.AccountService;
import com.example.account.service.RedisTestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)   // 이 컨트롤러만 격리시켜서 테스트 (단위테스트)
class AccountControllerTest {
    @MockBean  //가짜로 목 등록
    private AccountService accountService;  // AccountController가 의존하고있는 것들

    @MockBean
    private RedisTestService redisTestService;
    // -- 주입 완료

    @Autowired
    private MockMvc mockMvc;

    @Test
    void successGetAccount() throws Exception {
        //given
        given(accountService.getAccount(anyLong()))  // 목킹
                .willReturn(Account.builder()
                        .accountNumber("3456")
                        .accountStatus(AccountStatus.IN_USE)
                        .build());

        //when
        // 결과가 객체로 나오지 않기 때문에 일반적으로 하는 것(when에서 데이터 받고 then으로 비교)을 할 수 없음.
        //then  (결과 검증)
        mockMvc.perform(get("/account/876"))  // 컨트롤러 안에있는 URL 호출(스테틱 메소드로 가져옴)
                .andDo(print())
                .andExpect(jsonPath("$.accountNumber").value("3456"))  // 바디의 첫번째 구조에 있는 값
                .andExpect(jsonPath("$.accountStatus").value("IN_USE"))
                .andExpect(status().isOk());
    }
}