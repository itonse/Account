package com.example.account.controller;

import com.example.account.aop.AccountLock;
import com.example.account.dto.CancelBalance;
import com.example.account.dto.QueryTransactionResponse;
import com.example.account.dto.TransactionDto;
import com.example.account.dto.UseBalance;
import com.example.account.exception.AccountException;
import com.example.account.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 잔액 관련 컨트롤러
 * 1. 잔액 사용
 * 2. 잔액 사용 취소
 * 3. 거래 확인
 */
@Slf4j
@RestController  // 스프링의 빈으로 자동 등록
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping("/transaction/use")
    @AccountLock   // 동시성 제어 필요
    public UseBalance.Response useBalance(   // 응답 반환
            @Valid @RequestBody UseBalance.Request request  // 요청 필요
    ) throws InterruptedException {

        try {
            Thread.sleep(3000L);   // 스레드 슬립을 함 (3초) -> 거래 성공 응답을 3초 뒤에 함
            return UseBalance.Response.from(  // UseBalance 처리 후, 응답을 반환
                    transactionService.useBalance(request.getUserId(),
                            request.getAccountNumber(), request.getAmount())
            );
        } catch (AccountException e) {
            log.error("Failed to use balance. ");   // 의도적으로 만든 에러가 발생 시에는 로그 찍기

            transactionService.saveFailedUseTransaction(   // 실패건을 저장
                    request.getAccountNumber(),
                    request.getAmount()
            );

            throw e;   // 에러 부분을 던져주어서 발생한 에러 알려주기
        }
    }

    @PostMapping("/transaction/cancel")
    @AccountLock   // 동시성 제어 필요
    public CancelBalance.Response cancelBalance(   // 응답 반환
            @Valid @RequestBody CancelBalance.Request request  // 요청 필요
    ) {

        try {
            return CancelBalance.Response.from(  // UseBalance 처리 후, 응답을 반환
                    transactionService.cancelBalance(request.getTransactionId(),
                            request.getAccountNumber(), request.getAmount())
            );
        } catch (AccountException e) {
            log.error("Failed to use balance. ");   // 의도적으로 만든 에러가 발생 시에는 로그 찍기

            transactionService.saveFailedCancelTransaction(   // 실패건을 저장
                    request.getAccountNumber(),
                    request.getAmount()
            );

            throw e;   // 에러 부분을 던져주어서 발생한 에러 알려주기
        }
    }

    @GetMapping("/transaction/{transactionId}")
    public QueryTransactionResponse queryTransaction(
            @PathVariable String transactionId) {
        return QueryTransactionResponse.from(
                transactionService.queryTransaction(transactionId)
        );
    }
}
