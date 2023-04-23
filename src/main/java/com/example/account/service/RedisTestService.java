package com.example.account.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisTestService {
    // 아래 형식이여야 RequiredArgsConstructor 가 자동으로 여기에 포함된 생성자를 만들어줌 (자동 생성자 주입)
    private final RedissonClient redissonClient;
    // 같은 이름으로 RedisRepositoryConfig 에서 생성한 RedissonClient 빈이 여기에 자동으로 주입

    public String getLock() {   // 스핀락 획득 기능  (http://localhost:8080/get-lock 웹검색)
        RLock lock = redissonClient.getLock("sampleLock");  //redissonClient 의 락 기능

        try {  // 위에서 받은 락으로 시도
            boolean isLock = lock.tryLock(1, 5, TimeUnit.SECONDS);
            // 최대 1초 동안 기다리면서 이 락을 찾아봄. 락을 획득했으면 3초동안 가지고있다가 풀어준다.
            if (!isLock) {  // 락 획득에 실패
                log.error("========================== Lock acquisition failed============");
                return "Lock failed";
            }
        } catch (Exception e) {
            log.error("Redis lock failed");
        }

        return "Lock success";
    }
}
