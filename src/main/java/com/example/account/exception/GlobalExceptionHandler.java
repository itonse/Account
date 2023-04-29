package com.example.account.exception;

import com.example.account.dto.ErrorResponse;
import com.example.account.type.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.example.account.type.ErrorCode.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {   // 모든 Controller에서 발생했던 exception들을 여기서 처리함

    @ExceptionHandler(AccountException.class)   // AccountException에 대한 핸들러
    public ErrorResponse handleAccountException(AccountException e) {  // ErrorResponse 타입으로 응답을 줌. (사용자에게 나감)
        log.error("{} is ocurred.", e.getErrorCode());

        return new ErrorResponse(e.getErrorCode(), e.getErrorMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)   // 계좌 생성시 초기 잔액이 마이너스 금액일 때에 대한 에러처리
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException is ocurred.", e);

        return new ErrorResponse(INVALID_REQUEST, INVALID_REQUEST.getDescription());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)   // 그 외 (자바, 스프링 DB 등 에서 )자주 발생하는 에러들에 대한 처리
    public ErrorResponse handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error("DataIntegrityViolationException is ocurred.", e);

        return new ErrorResponse(INVALID_REQUEST, INVALID_REQUEST.getDescription());
    }

    @ExceptionHandler(Exception.class)    // 가장 최종적으로 발생하는 모든 익셉션에 대한 처리 (필수)
    public ErrorResponse handleException(Exception e) {
        log.error("Exception is ocurred.", e);

        return new ErrorResponse(
                INTERNAL_SERVER_REQUEST,
                INTERNAL_SERVER_REQUEST.getDescription());  // 서버에서 예상치 못한 모르는 익셉션 발생
    }
}
