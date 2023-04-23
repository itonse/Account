package com.example.account.service;

import com.example.account.domain.Account;
import com.example.account.domain.AccountStatus;
import com.example.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service  // 서비스타입 빈으로 스프링에 자동으로 등록
@RequiredArgsConstructor  // 꼭 필요한 args가 들어간 생성자를 만듦
public class AccountService {   // 계좌 서비스
    private final AccountRepository accountRepository;  // 해당 빈을 다른 빈에 넣을 수 있게 final로

    @Transactional
    public void createAccount() {   // Account 엔티티 생성
        Account account = Account.builder()   // Account 에 @Builder를 달아서 사용 가능
                .accountNumber("40000")
                .accountStatus(AccountStatus.IN_USE)
                .build();
        accountRepository.save(account);  // 어카운트 레파지토리에 저장
    }
    // http://localhost:8080/create-account 웹검색하면 엔티티 생성

    @Transactional
    public Account getAccount(Long id) {  // Account에 id의 데이터 가져오기
        if (id < 0) {
            throw new RuntimeException("Minus");
        }
        return accountRepository.findById(id).get();   // findById로 id의 데이터를 SELECT해서 가져옴.
    }
    // http://localhost:8080/account/1 웹에서 하면 1번 데이터의 내용이 화면에 표시

}
