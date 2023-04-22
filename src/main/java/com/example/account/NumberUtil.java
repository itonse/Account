package com.example.account;


import lombok.experimental.UtilityClass;

@UtilityClass
public class NumberUtil {
    //private NumberUtil(){};  // 생성자를 private으로 만들어서 생성자를 쓰지 못하도록 막음.
    // -> UtilityClass 가 이 것을 대신 해줌

    // 따로 객체 생성 필요 없는 모두 정적인(static) 메소드
    public static Integer sum(Integer a, Integer b) {
        return a + b;
    }

    public static Integer minus(Integer a, Integer b) {
        return a - b;
    }
}
