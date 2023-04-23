package com.example.account.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Configuration // 빈 등록
public class LocalRedisConfig {
    @Value("${spring.redis.port}")  // 값을 이 경로에서 가져옴
    private int redisPort; // 레디스를 띄어줄 포트

    private RedisServer redisServer;    // 레디스 임베디드에 있는 레디스 서버

    @PostConstruct  // 빈을 등록하면서 자동으로 레디스를 스타트 시킴
    public void start() {   // 메소드 이름은 무관
        redisServer = new RedisServer(redisPort);  // 레디스 서버에 레디스 포트를 넣어줌.
        redisServer.start();  // 레디스서버 시작
    }

    @PreDestroy   // 레디스가 꺼질때 (빈을 파괴할 때) 레디스가 자동으로 꺼짐
    public void stopRedis() {
        if (redisServer != null) {   // 레디스 서버가 생성이 잘 됐을 때
            redisServer.stop();   // 레디스가 종료
        }
    }
}
