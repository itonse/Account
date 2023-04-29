package com.example.account.service;

import com.example.account.aop.AccountLockIdInterface;
import com.example.account.dto.UseBalance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class LockAopAspect {  //
    private final LockService lockService;   // 락서비스를 가져옴
    // 커스텀 어노테이션 생성
    @Around("@annotation(com.example.account.aop.AccountLock) && args(request)")
        // 어떤 경우에 이 Aspect를 적용 할 것인가를 정의, @AccountLock 을 붙인 메소드들에 있는 request 파라미터를 가져옴
    public Object aroundMethod(
            ProceedingJoinPoint pjp,    // 진행하고 있던 join point
            AccountLockIdInterface request   // UseBalance, CancelBalance 상관없이 공통화된 인터페이스로 가져옴.
    ) throws Throwable {   // 예외는 던짐
        // lock 취득 시도
        lockService.lock(request.getAccountNumber());   // 락을 가져옴
        try{
            return pjp.proceed();    // 진행하고 있던 join point를 가져와서 진행시킴
        } finally {
            // lock해제 (그 동작이 정상적으로 진행이 되든, 실패를 하든)
            lockService.unlock(request.getAccountNumber());
        }
    }
}
