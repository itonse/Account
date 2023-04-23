package com.example.account.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisRepositoryConfig {  // 레디스를 접근하기 위한 클라이언트
    @Value("${spring.redis.host}")  // 경로에서 호스트값을 가져옴 (127.0.0.1)
    private String redisHost;  // 레디스 포트 (6379)

    @Value("${spring.redis.port}")
    private int redisPort;

    @Bean  // (로컬에) 레디슨클라이언트를 빈으로 등록
    public RedissonClient redissonClient() {  // 레디스 클라이언트
        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + redisHost + ":" + redisPort);

        return Redisson.create(config);
    }
}
