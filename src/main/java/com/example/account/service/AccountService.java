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
        AccountUser accountUser = accountUserRepository.findById(userId)  // 조회를 했을때 나오는 타입: Opional
                .orElseThrow(() -> new AccountException(ErrorCode.USER_NOT_FOUND));  // accountUser가 없으면 예외로 던져짐, 있으면 accountUser에 저장

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


    @Transactional
    public Account getAccount(Long id) {  // Account에 id의 데이터 가져오기
        if (id < 0) {
            throw new RuntimeException("Minus");
        }
        return accountRepository.findById(id).get();   // findById로 id의 데이터를 SELECT해서 가져옴.
    }
}
