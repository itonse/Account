package com.example.account.service;

import com.example.account.exception.AccountException;
import com.example.account.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class LockService {
    private final RedissonClient redissonClient;

    public void lock(String accountNumber) {
        RLock lock = redissonClient.getLock(getLockKey(accountNumber));  // 받아온 계좌번호 자체를 락의 키로 설정
        log.debug("Trying lock for accountNumber : {}", accountNumber);    // 로그로 디버깅으로 심음

        try {  // 위에서 받은 락으로 시도
            boolean isLock = lock.tryLock(1, 15, TimeUnit.SECONDS);
            //1초 동안 아무작업 안 하면 자동으로 락이 풀리게 되고,
            //15초 동안 기다려봤을 때까지 락이 안 풀려있으면, 다시 락 취득을 못 함
            if (!isLock) {  // 락 획득에 실패하면,
                log.error("========================== Lock acquisition failed============");
                throw new AccountException(ErrorCode.ACCOUNT_TRANSACTION_LOCK);  // 거래 실패
            }
        } catch (AccountException e) {  // AccountException에 대한 에러는 에러 응답에 활용할 수 있도록 글로벌익셉션으로 던짐
            throw e;
        } catch (Exception e) {   // 그 외에 락을 가져오지 못해 발생하는 에러가 아닌 기타 에러는
            log.error("Redis lock failed", e);   // 로그에 에러만 찍히도록 함.
        }
    }

    public void unlock(String accountNumber) {   // 언락
        log.debug("Unlock for accountNumber : {} " + accountNumber);  // accountNumber에 대해서 락을 푼다는 것을 알려줌.
        redissonClient.getLock(getLockKey(accountNumber)).unlock();   // 락을 가져온 후, 이것을 언락으로 풀어줌
    }

    private static String getLockKey(String accountNumber) {
        return "ACLK:" + accountNumber;
    }
}

