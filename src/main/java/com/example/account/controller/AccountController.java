package com.example.account.controller;

import com.example.account.domain.Account;
import com.example.account.service.AccountService;
import com.example.account.service.RedisTestService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController   // 이 컨트롤러로 빈으로 등록
@RequiredArgsConstructor
public class AccountController {   // controller에 호출을 시킬 수 있는 end-Point 생성
    private final AccountService accountService;  // 의존성 주입 받음
    // 접속순서: 외부->AccountController->AccountService->AccountRepository (의존관계, 순차적, 계층화)
    private final RedisTestService redisTestService;

    @GetMapping("/get-lock")  // 엔드포인트
    public String getLock() {
        return redisTestService.getLock();
    }

    @GetMapping("/create-account")   // create-account API 생성
    public String createAccount() {
        accountService.createAccount();
        return "success";
    }

    @GetMapping("/account/{id}")
    public Account getAccount(
            @PathVariable Long id) {  // id를 받음
        return accountService.getAccount(id); // getAccount를 id로 호출
            // Account 테이블에서 id로 SELECT를 하고, 그 값을 응답으로 받음
    }
}
