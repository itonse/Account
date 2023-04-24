package com.example.account.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration   // 자동 빈 등록
@EnableJpaAuditing  // DB에 데이터 저장&업데이트를 할 때 Account에서 어노테이션이 붙은 값들을 자동으로 저장
public class JpaAuditingConfiguration {  // 자동 회계 관련 Configuration 진행
}
