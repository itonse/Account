package com.example.account.service;


import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import com.example.account.domain.Transaction;
import com.example.account.dto.TransactionDto;
import com.example.account.exception.AccountException;
import com.example.account.repository.AccountRepository;
import com.example.account.repository.AccountUserRepository;
import com.example.account.repository.TransactionRepository;
import com.example.account.type.AccountStatus;
import com.example.account.type.ErrorCode;
import com.example.account.type.TransactionResultType;
import com.example.account.type.TransactionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import static com.example.account.type.TransactionResultType.*;
import static com.example.account.type.TransactionType.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;   // 인젝션
    private final AccountUserRepository accountUserRepository;
    private final AccountRepository accountRepository;

    /**
     * 사용자 없는 경우, 계좌가 없는 경우,
     * 사용자 아이디와 계좌 소유주가 다른 경우,
     * 계좌가 이미 해지 상태인 경우, 거래금액이 잔액보다 큰 경우,
     * 거래금액이 너무 작거나 큰 경우 실패 응답  : 이미 Request 에서 Validation 완료.
     */
    @Transactional
    public TransactionDto useBalance(Long userId, String accountNumber,
                                     Long amount) {
        AccountUser user = accountUserRepository.findById(userId)  // 사용자 조회해서 없는지 확인
                .orElseThrow(() -> new AccountException(ErrorCode.USER_NOT_FOUND));
        Account account = accountRepository.findByAccountNumber(accountNumber)  // 계좌 조회해서 없는지 확인
                        .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));

        validateUseBalance(user, account, amount);

        account.useBalance(amount);   // account의 잔액을 바꿔주는 것을 account 엔티티 안에서 직접 처리(더 안전한 객체 구조.)
            // Transaction 어노테이션으로 인해, 아래 return에서 오류가 생긴 경우 잔액이 업데이트 되지 않고 rollback이 됨. 성공할 경우 반영

        return TransactionDto.fromEntity(saveAndGetTransaction(USE, S, account, amount));   // USE로 성공건 저장
    }

    private void validateUseBalance(AccountUser user, Account account, Long amount) {
        if (!Objects.equals(user.getId(), account.getAccountUser().getId())) {
            throw new AccountException(ErrorCode.USER_ACCOUNT_UN_MATCH);  // 사용자 아이디와 계좌 소유주가 다를 경우
        }
        if (account.getAccountStatus() != AccountStatus.IN_USE) {  // 계좌가 이미 해지상태인 경우
            throw new AccountException(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED);
        }
        if (account.getBalance() < amount) {
            throw new AccountException(ErrorCode.AMOUNT_EXCEED_BALANCE);  // 거래금액이 잔액보다 큼
        }
    }

    @Transactional
    public void saveFailedUseTransaction(String accountNumber, Long amount) {  // 잔액 사용 실패 트랜잭션 저장
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));  // 계좌를 못 찾았다면 거래로 남기는 것을 포기.

        saveAndGetTransaction(USE, F, account, amount);    // 실패건 저장
    }

    private Transaction saveAndGetTransaction(   // 저장된 트랜잭션 엔티티를 응답해주는 공통 메소드
            TransactionType transactionType,
            TransactionResultType transactionResultType,
            Account account,
            Long amount) {
        return transactionRepository.save(  // account를 기반으로 신규 transaction을 하나 저장.
                Transaction.builder()
                        .transactionType(transactionType)
                        .transactionResultType(transactionResultType)
                        .account(account)
                        .amount(amount)
                        .balanceSnapshot(account.getBalance())
                        .transactionId(UUID.randomUUID().toString().replace("-", ""))
                        .transactedAt(LocalDateTime.now())
                        .build()
        );
    }

    @Transactional
    public TransactionDto cancelBalance(
            String transactionId,
            String accountNumber,
            Long amount
    ) {
        Transaction transaction = transactionRepository.findByTransactionId(transactionId)   // 트랜잭션 찾기
                .orElseThrow(() -> new AccountException(ErrorCode.TRANSACTION_NOT_FOUND));  // 해당 거래가 없습니다.
        Account account = accountRepository.findByAccountNumber(accountNumber)  // 계좌 찾기
                .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));  // 계좌가 없습니다.

        validateCancelBalance(transaction,account, amount);

        account.cancelBalance(amount);  // account의 잔액을 바꿔주는 것을 account 엔티티 안에서 직접 처리(더 안전한 객체 구조.)
        // Transaction 어노테이션으로 인해, 아래 return에서 오류가 생긴 경우 잔액이 업데이트 되지 않고 rollback이 됨. 성공할 경우 반영

        return TransactionDto.fromEntity(
                saveAndGetTransaction(CANCEL, S, account, amount));   // CANCEL로 성공을 저장
    }

    private void validateCancelBalance(Transaction transaction, Account account, Long amount) {
        if (!Objects.equals(transaction.getAccount().getId(), account.getId())) {  // 트랜잭션의 account의 id가 account의 id와 다를 때
            throw new AccountException(ErrorCode.TRANSACTION_ACCOUNT_UN_MATCH);  // 이 거래는 해당 계좌에서 발생한 거래가 아닙니다.
        }
        if (!Objects.equals(transaction.getAmount(), amount)) {    // 트랜잭션의 amount와 amount가 다를 경우
            throw new AccountException(ErrorCode.CANCEL_MUST_FULLY);   // 부분 취소는 허용X
        }
        if (transaction.getTransactedAt().isBefore(LocalDateTime.now().minusYears(1))) {   // 거래시일이 1년 보다 이전이면
            throw new AccountException(ErrorCode.TOO_OLD_ORDER_TO_CANCEL);
        }
    }

    @Transactional
    public void saveFailedCancelTransaction(String accountNumber, Long amount) {  // 잔액 사용 취소 실패 트랜잭션 저장
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));  // 계좌를 못 찾았다면 거래로 남기는 것을 포기.

        saveAndGetTransaction(CANCEL, F, account, amount);    // 실패건 저장
    }
}
