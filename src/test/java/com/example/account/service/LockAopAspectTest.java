package com.example.account.service;

import com.example.account.dto.UseBalance;
import com.example.account.exception.AccountException;
import com.example.account.type.ErrorCode;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.example.account.type.ErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LockAopAspectTest {
    @Mock
    private LockService lockService;

    @Mock
    private ProceedingJoinPoint proceedingJoinPoint;

    @InjectMocks   // 위의 Mock 2개를 여기에 주입
    private LockAopAspect lockAopAspect;

    @Test
    void lockAndUnlock() throws Throwable {    // 락을 수행하고 언락을 하는지 확인
        //given
        ArgumentCaptor<String> lockArgumentCaptor =   // 락 된 계좌번호(accountNumber)를 캡처할 도구
                ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> unlockArgumentCaptor =    // 언락 된 계좌번호를 캡처할 도구
                ArgumentCaptor.forClass(String.class);
        UseBalance.Request request =
                new UseBalance.Request(123L, "1234", 1000L);

        //when
        lockAopAspect.aroundMethod(proceedingJoinPoint, request);
            // lockAopAspect 에서 aroundMethod로 proceedingJoinPoint와 특정 request가 갔을 때

        //then
        verify(lockService, times(1)).lock(lockArgumentCaptor.capture());
            // lock 될 때 lockArgumentCaptor 가 계좌번호를 캡처
        verify(lockService, times(1)).unlock(unlockArgumentCaptor.capture());
            // unlock 될 때 unlockArgumentCaptor 가 계좌번호를 캡처
        assertEquals("1234", lockArgumentCaptor.getValue());
        assertEquals("1234", unlockArgumentCaptor.getValue());
    }

    @Test
    void lockAndUnlock_evenIfThrow() throws Throwable {    // 계좌를 못 찾는 익셉션이 발생해도, 락,언락을 수행 하는지 확인
        //given
        ArgumentCaptor<String> lockArgumentCaptor =   // 락 된 계좌번호(accountNumber)를 캡처할 도구
                ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> unlockArgumentCaptor =    // 언락 된 계좌번호를 캡처할 도구
                ArgumentCaptor.forClass(String.class);
        UseBalance.Request request =
                new UseBalance.Request(123L, "54321", 1000L);
        given(proceedingJoinPoint.proceed())
                .willThrow(new AccountException(ACCOUNT_NOT_FOUND));  // ACCOUNT_NOT_FOUND 를 던질 때
        //when
        assertThrows(AccountException.class, ()->
        lockAopAspect.aroundMethod(proceedingJoinPoint, request));   // 여기서 발생한 익셉션을 잡아줌

        //then
        verify(lockService, times(1)).lock(lockArgumentCaptor.capture());
        // lock 될 때 lockArgumentCaptor 가 계좌번호를 캡처
        verify(lockService, times(1)).unlock(unlockArgumentCaptor.capture());
        // unlock 될 때 unlockArgumentCaptor 가 계좌번호를 캡처
        assertEquals("54321", lockArgumentCaptor.getValue());
        assertEquals("54321", unlockArgumentCaptor.getValue());
    }
}