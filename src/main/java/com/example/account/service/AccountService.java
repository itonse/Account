package com.example.account.service;

import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import com.example.account.dto.AccountDto;
import com.example.account.exception.AccountException;
import com.example.account.repository.AccountRepository;
import com.example.account.repository.AccountUserRepository;
import com.example.account.type.AccountStatus;
import com.example.account.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

import static com.example.account.type.AccountStatus.IN_USE;

@Service  // 서비스타입 빈으로 스프링에 자동으로 등록
@RequiredArgsConstructor  // 꼭 필요한 args가 들어간 생성자를 만듦
public class AccountService {   // 계좌 서비스
    private final AccountRepository accountRepository;  // 해당 빈을 다른 빈에 넣을 수 있게 final로
    private final AccountUserRepository  accountUserRepository;  // 테이블 인젝션 (사용자 조회)

    /** 계좌 생성
     * 사용자가 있는지 조회
     * 계좌의 번호를 생성하고
     * 계좌를 저장하고, 그 정보를 넘긴다.
     */
    @Transactional
    public AccountDto createAccount(Long userId, Long initialBalance) {   // 계좌 생성
        AccountUser accountUser = accountUserRepository.findById(userId)  // userId로 findById 해서 없으면 USER_NOT_FOUND 던짐, 있으면 accountUser에 저장
                .orElseThrow(() -> new AccountException(ErrorCode.USER_NOT_FOUND));

        validateCreateAccount(accountUser);  // 예외처리 메소드

        String newAccountNumber = accountRepository.findFirstByOrderByIdDesc()  // 제일 마지막에 생성된 계좌를 가져옴
                .map(account -> (Integer.parseInt(account.getAccountNumber())) + 1 + "")
                    // 존재한다면 AccountNumber를 받아서 이것보다 하나 더 큰 숫자 넣어줌 (문자->숫자->문자 변환)
                .orElse("1000000000");  // 없으면은(최초 생성) 이 계좌번호 부여

        return AccountDto.fromEntity(   // 새 accountRepository를 만들어서 AccountDto로 변환후 컨트롤러로 넘김
                accountRepository.save(Account.builder()   // 여기서 생성한 어카운트 엔티티를 accountRepository을 통해 저장.
                        .accountUser(accountUser)
                        .accountStatus(IN_USE)  // 깔끔한 코드를 위해 정적 import 함.
                        .accountNumber(newAccountNumber)
                        .balance(initialBalance)
                        .registeredAt(LocalDateTime.now())
                        .build())
        );
    }

    private void validateCreateAccount(AccountUser accountUser) {
        if (accountRepository.countByAccountUser(accountUser) >= 10) {  // 이 사람이 소유하고있는 계좌의 개수가 이미 10개 (이상)이면,
            throw new AccountException(ErrorCode.MAX_ACCOUNT_PER_USER_10);  // 이 예외를 발생시킴
        }
    }


    @Transactional
    public Account getAccount(Long id) {  // Account에 id의 데이터 가져오기
        if (id < 0) {
            throw new RuntimeException("Minus");
        }
        return accountRepository.findById(id).get();   // findById로 id의 데이터를 SELECT해서 가져옴.
    }

    @Transactional
    public AccountDto deleteAccount(Long userId, String accountNumber) {
        AccountUser accountUser = accountUserRepository.findById(userId)  // AccountUser 찾기
                .orElseThrow(() -> new AccountException(ErrorCode.USER_NOT_FOUND));   // userId로 findById 해서 없으면 USER_NOT_FOUND 던짐, 있으면 accountUser 에 저장
        Account account = accountRepository.findByAccountNumber(accountNumber)  // Account 찾기
                .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));   // accountNumber로 findByAccountNumber 해서 없으면 ACCOUNT_NOT_FOUND 던짐, 있으면 account 에 저장

        validateDeleteAccount(accountUser, account);  // 계좌해지 예외 검사하기 (3가지)

        account.setAccountStatus(AccountStatus.UNREGISTERED);   // 계좌상태를 해지상태로 업데이트
        account.setUnRegisteredAt(LocalDateTime.now());   // 해지한 시간을 현재 시간으로 업데이트

        accountRepository.save(account);   // (불필요한 코드지만) account를 일부로 호출 해서 이 account에 UNREGISTERED 상태값이 들어왔는지 확인하기

        return AccountDto.fromEntity(account);   // AccountDto 를 account 로부터 만들어서 응답을 줌.
    }

    private void validateDeleteAccount(AccountUser accountUser, Account account) {   // 계좌해지 예외 검사
        if (!Objects.equals(accountUser.getId(), account.getAccountUser().getId())) {   // 사용자ID와 계좌 소유주가 다른 경우
            throw new AccountException(ErrorCode.USER_ACCOUNT_UN_MATCH);  // USER_ACCOUNT_UN_MATCH 에러코드 던짐
        }
        if (account.getAccountStatus() == AccountStatus.UNREGISTERED) {   // 이미 계좌가 해지 상태인 경우
            throw new AccountException(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED);  // 해당 에러코드 던짐

        }
        if (account.getBalance() > 0) {     // 계좌 잔액이 있는 경우
            throw new AccountException(ErrorCode.BALANCE_NOT_EMPTY);  // 해당 에러코드 던짐
        }
    }
}
