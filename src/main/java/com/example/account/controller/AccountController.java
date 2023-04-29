package com.example.account.controller;

import com.example.account.domain.Account;
import com.example.account.dto.AccountInfo;
import com.example.account.dto.CreateAccount;
import com.example.account.dto.DeleteAccount;
import com.example.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController   // 이 컨트롤러로 빈으로 등록
@RequiredArgsConstructor
public class AccountController {   // controller에 호출을 시킬 수 있는 end-Point 생성
    private final AccountService accountService;  // 의존성 주입 받음
    // 접속순서: 외부->AccountController->(AccountDto)->AccountService->AccountRepository->Acount(의존관계, 순차적, 계층화)

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

    @DeleteMapping("/account")
    public DeleteAccount.Response deleteAccount(  // 응답을 받음
            @RequestBody @Valid DeleteAccount.Request request
    ) {
        return DeleteAccount.Response.from(   //
                accountService.deleteAccount(   // 받아온 AccountDto를 DeleteAccount.Response로 변환해서 응답 생성
                        request.getUserId(),
                        request.getAccountNumber()
                )
        );
    }

    @GetMapping("/account")
    public List<AccountInfo> getAccountsByUserId(
            @RequestParam("user_id") Long userId
    ) {
        return accountService.getAccountsByUserId(userId)   // List<AccountInfo> -> List<AccountInfo> 형변환
                .stream().map(accountDto ->
                        AccountInfo.builder()
                        .accountNumber(accountDto.getAccountNumber())
                        .balance(accountDto.getBalance())
                        .build())
                .collect(Collectors.toList());
    }

    @GetMapping("/account/{id}")
    public Account getAccount(
            @PathVariable Long id) {  // id를 받음
        return accountService.getAccount(id); // getAccount를 id로 호출
        // Account 테이블에서 id로 SELECT를 하고, 그 값을 응답으로 받음
    }
}

