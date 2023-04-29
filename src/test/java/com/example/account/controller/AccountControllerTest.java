package com.example.account.controller;

import com.example.account.domain.Account;
import com.example.account.dto.AccountDto;
import com.example.account.dto.CreateAccount;
import com.example.account.dto.DeleteAccount;
import com.example.account.exception.AccountException;
import com.example.account.type.AccountStatus;
import com.example.account.service.AccountService;
import com.example.account.type.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)   // 이 컨트롤러만 격리시켜서 테스트 (단위테스트)
class AccountControllerTest {    // 테스트 컨테이너
    @MockBean  // 가짜 accountService 를 목 등록
    private AccountService accountService;  // AccountController가 의존하고있는 것들

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;   // (변환기) object -> json -> 문자열


    @Test
    void successCreateAccount() throws Exception {  // 계좌생성 성공 테스트
        //given
        given(accountService.createAccount(anyLong(), anyLong()))   // mocking이 어떤 입력값이 들어오든간에 동일한 값이 나옴
                .willReturn(AccountDto.builder()  // createAccount는 해당 AccountDto을 응답할 것이다.
                        .userId(1L)
                        .accountNumber("1234567890")
                        .registeredAt(LocalDateTime.now())
                        .unRegisteredAt(LocalDateTime.now())
                        .build());
        //when
        //then
        mockMvc.perform(post("/account")   // mockMvc에 post로 /account 라는 곳에 요청을 날리기
                .contentType(MediaType.APPLICATION_JSON)  // 들어가는 타입은 json
                .content(objectMapper.writeValueAsString(  // objectMapper를 이용해서 특정값(json)을 문자열로 만들기
                        new CreateAccount.Request(3333L, 1111L)  // json 생성
                )))   // Handler로 AccountControoler가 찾아지고, 그 안에 createAccount 메소드가 호출 됨.
                .andExpect(status().isOk())  // 기대되는 결과: 200 ok
                .andExpect(jsonPath("$.userId").value(1))   // (기대) 응답 바디에 오는 userId의 값은 1
                .andExpect(jsonPath("$.accountNumber").value("1234567890")) // (기대) 응답 바디에 오는 계좌번호는 "1234567890"
                .andDo(print());  // 콘솔창에 요청에 대한 응답 정보 표시됨 (손쉽게 확인 가능)

    }

    @Test
    void successDeleteAccount() throws Exception {  // 계좌생성 성공 테스트
        //given
        given(accountService.deleteAccount(anyLong(), anyString()))   // deleteAccount(유저아이디, 계좌번호) Mocking
                .willReturn(AccountDto.builder()
                        .userId(1L)
                        .accountNumber("1234567890")
                        .registeredAt(LocalDateTime.now())
                        .unRegisteredAt(LocalDateTime.now())
                        .build());
        //when
        //then
        mockMvc.perform(delete("/account")
                        .contentType(MediaType.APPLICATION_JSON)  // 들어가는 타입은 json
                        .content(objectMapper.writeValueAsString(  // objectMapper를 이용해서 특정값(json)을 문자열로 만들기
                                new DeleteAccount.Request(3333L, "0987654321")  // json 생성
                        )))
                .andExpect(status().isOk())  // 기대되는 결과: 200 ok
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.accountNumber").value("1234567890"))
                .andDo(print());

    }

    @Test
    void successGetAccountsByUserId() throws Exception {
        //given
        List<AccountDto> accountDtos =
                Arrays.asList(
                        AccountDto.builder()
                                .accountNumber("1234567890")
                                .balance(1000L).build(),
                        AccountDto.builder()
                                .accountNumber("1111111111")
                                .balance(2000L).build(),
                        AccountDto.builder()
                                .accountNumber("2222222222")
                                .balance(3000L).build()
                );
        given(accountService.getAccountsByUserId(anyLong()))
                .willReturn(accountDtos);

        //when
        //then
        mockMvc.perform(get("/account?user_id=1"))
                .andDo(print())
                .andExpect(jsonPath("$[0].accountNumber").value("1234567890"))    // 루트경로에서 0번째 리스트의 계좌번호
                .andExpect(jsonPath("$[0].balance").value(1000))    // 루트경로에서 0번째 리스트의 계좌 잔액
                .andExpect(jsonPath("$[1].accountNumber").value("1111111111"))
                .andExpect(jsonPath("$[1].balance").value(2000))
                .andExpect(jsonPath("$[2].accountNumber").value("2222222222"))
                .andExpect(jsonPath("$[2].balance").value(3000));
    }

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
                .andExpect(jsonPath("$.accountNumber").value("3456"))  // 바디로 출력되는 값
                .andExpect(jsonPath("$.accountStatus").value("IN_USE"))
                .andExpect(status().isOk());
    }

    @Test
    void failGetAccount() throws Exception {   // (위의 테스트와 MockHttpServletResponse의 바디 값 비교해보기)
        //given
        given(accountService.getAccount(anyLong()))  // 목킹
                .willThrow(new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));

        //when
        // 결과가 객체로 나오지 않기 때문에 일반적으로 하는 것(when에서 데이터 받고 then으로 비교)을 할 수 없음.
        //then  (결과 검증)
        mockMvc.perform(get("/account/876"))  // 컨트롤러 안에있는 URL 호출(스테틱 메소드로 가져옴)
                .andDo(print())
                .andExpect(jsonPath("$.errorCode").value("ACCOUNT_NOT_FOUND"))  // 바디로 출력되는 값 (차이점)
                .andExpect(jsonPath("$.errorMessage").value("계좌가 없습니다."))  // "
                .andExpect(status().isOk());
    }
}