package com.example.account.service;

import com.example.account.exception.AccountException;
import com.example.account.type.ErrorCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class LockServiceTest {
    @Mock
    private RedissonClient redissonClient;

    @Mock
    private RLock rLock;

    @InjectMocks
    private  LockService lockService;

    @Test
    void succesGetLock() throws InterruptedException {   // 락 취득 성공
        //given
        given(redissonClient.getLock(anyString()))
                .willReturn(rLock);
        given(rLock.tryLock(anyLong(), anyLong(), any()))
                .willReturn(true);  // rLock이 tryLock을 했을 때 true가 나옴.

        //when
        //then
        assertDoesNotThrow(()-> lockService.lock("123"));  // 락서비스에 락을 요청했을 때
            // 락을 취득해서 아무 동작도 안함.
    }

    @Test
    void failGetLock() throws InterruptedException {   // 락 취득 실패
        //given
        given(redissonClient.getLock(anyString()))
                .willReturn(rLock);
        given(rLock.tryLock(anyLong(), anyLong(), any()))
                .willReturn(false);  // rLock이 tryLock을 했을 때 응답 실패.

        //when
        AccountException exception = assertThrows(AccountException.class,
                () -> lockService.lock("123"));  // 락서비스에 락을 요청했을 때
            // 락을 취득 못 해서 throw를 해서 excetion에 저장.
//
        //then
        assertEquals(ErrorCode.ACCOUNT_TRANSACTION_LOCK, exception.getErrorCode());
            // excetion에서 발생하는 에러코드는 해당 계좌는 사용중 이다.
    }
}