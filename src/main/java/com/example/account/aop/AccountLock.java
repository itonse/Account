package com.example.account.aop;

import java.lang.annotation.*;

@Target(ElementType.METHOD)    // 어노테이션을 붙일 수 있는 타겟
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited    // 상속 가능한 구조로 설정
public @interface AccountLock {
    long tryLockTIme() default 5000L;    // 해당 시간 동안 기다려주겠다 (5초)
}
