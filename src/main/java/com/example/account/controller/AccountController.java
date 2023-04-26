package com.example.account.controller;

import com.example.account.domain.Account;
import com.example.account.dto.CreateAccount;
import com.example.account.service.AccountService;
import com.example.account.service.RedisTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController   // 이 컨트롤러로 빈으로 등록
@RequiredArgsConstructor
public class AccountController {   // controller에 호출을 시킬 수 있는 end-Point 생성
    private final AccountService accountService;  // 의존성 주입 받음
    // 접속순서: 외부->AccountController->(AccountDto)->AccountService->AccountRepository (의존관계, 순차적, 계층화)
    private final RedisTestService redisTestService;

    @PostMapping("/account")
    public CreateAccount.Response createAccount(  // 응답을 받음
            @RequestBody @Valid CreateAccount.Request request
    ) {
        return CreateAccount.Response.from(   //
                accountService.createAccount(   // 받아온 AccountDto를 CreateAccount.Response로 변환해서 응답 생성
                        request.getUserId(),
                        request.getInitialBalance()
                )
        );
    }

    @GetMapping("/get-lock")  // 엔드포인트
    public String getLock() {
        return redisTestService.getLock();
    }

    @GetMapping("/account/{id}")
    public Account getAccount(
            @PathVariable Long id) {  // id를 받음
        return accountService.getAccount(id); // getAccount를 id로 호출
        // Account 테이블에서 id로 SELECT를 하고, 그 값을 응답으로 받음
    }
}

