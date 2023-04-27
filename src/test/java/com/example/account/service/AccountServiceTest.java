package com.example.account.service;

import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import com.example.account.dto.AccountDto;
import com.example.account.exception.AccountException;
import com.example.account.repository.AccountUserRepository;
import com.example.account.type.AccountStatus;
import com.example.account.repository.AccountRepository;
import com.example.account.type.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.DoNotMock;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)   // Mockto: DB의 데이터나 의존관계 바뀜 등 상관없이 온전이 맏은 역할만 테스트 함.
class AccountServiceTest {   // 하위에 AccountRepository 의존성을 갖고있음.

    @Mock
    private AccountRepository accountRepository;
    // 가짜로 accountRepository을 생성하여 Mock으로 만듦

    @Mock
    private AccountUserRepository accountUserRepository;
    // 가짜로 accountUserRepository 를 생성하여 Mock으로 만듦

    @InjectMocks  // 위 두 개의 Mock이 달려있는 accountService 가 생성이 되어서 들어감
    private AccountService accountService;  // 위 두 개의 Mock이 달려있는 accountService 가 생성이 되어서 들어감

    @Test   // 계좌 생성이 최초가 아닐 경우
    void createAccountSuccess() {   // findById, findFirstByOrderByIdDesc, save에 대한 Mocking이 모두 되어있어야만 함.
        //given
        AccountUser user = AccountUser.builder()  // 사용될 변수 user
                .id(12L)
                .name("Pobi").build();
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));
        given(accountRepository.findFirstByOrderByIdDesc())
                .willReturn(Optional.of(Account.builder()
                                .accountUser(user)   // 하위에있는 accountUser 담기
                                .accountNumber("1000000012").build()));  // 현재까지 저장된 가장 마지막 계좌번호는 12

        given(accountRepository.save(any()))   // 새로 만들어지는 Account는 여기서 응답으로 줌
                .willReturn(Account.builder()
                        .accountUser(user)
                        .accountNumber("1000000013").build());  // 위에서 1000000012 을 주었으니 1000000013 으로 저장될 것
        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
            // accountRepository.save 에 뭘 save 하는지 확인하기
        //when
        AccountDto accountDto = accountService.createAccount(1L, 1000L);
            // 이 accountDto에는 userId, 계좌번호, 계좌생성일, 계좌폐기일 정보가 담김
        //then
        verify(accountRepository, times(1)).save(captor.capture());
            // accountRepository가 한 번 저장을 할 것이고, 저장을 할 때를 캡쳐해감
        assertEquals(12L, accountDto.getUserId());
        assertEquals("1000000013", captor.getValue().getAccountNumber());  // 원하는 결과값과 위에서 캡쳐해간 값과 비교 (주어진 1000000012 보다 1 큰값이여야 함)
    }

    @Test   // 계좌 생성이 최초일 경우
    void createFirstAccountSuccess() {
        //given
        AccountUser user = AccountUser.builder()  // 사용될 변수 user
                .id(15L)
                .name("Pobi").build();
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));
        given(accountRepository.findFirstByOrderByIdDesc())
                .willReturn(Optional.empty());  // 이전에 생성된 계좌가 없는 경우

        given(accountRepository.save(any()))   // 새로 만들어지는 Account는 여기서 응답으로 줌
                .willReturn(Account.builder()
                        .accountUser(user)
                        .accountNumber("1000000013").build());
        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        // accountRepository.save 에 뭘 save 하는지 확인하기
        //when
        AccountDto accountDto = accountService.createAccount(1L, 1000L);
        // 이 accountDto에는 userId, 계좌번호, 계좌생성일, 계좌폐기일 정보가 담김
        //then
        verify(accountRepository, times(1)).save(captor.capture());
        // accountRepository가 한 번 저장을 할 것이고, 저장을 할 때를 캡쳐해감
        assertEquals(15L, accountDto.getUserId());
        assertEquals("1000000000", captor.getValue().getAccountNumber());  // 원하는 결과값과 위에서 캡쳐해간 값과 비교
    }

    @Test
    @DisplayName("해당 유저 없음 - 계좌 생성 실패")
    void createAccount_UserNotFound() {
        //given
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.empty());   // 찾고자 하는 유저가 없음 (텅빈 optional이 넘어옴)

        //when
        AccountException exception = assertThrows(AccountException.class,   // 해당 logic은 accountException을 던질 것.
                () -> accountService.createAccount(1L, 1000L));

        //then
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());   // 그 exception의 에러코드는 USER_NOT_FOUND 일 것.
    }

    @Test
    @DisplayName("유저 당 최대 계좌는 10개")
    void createAccount_maxAccountIs10() {
        //given
        AccountUser user = AccountUser.builder()  // 사용될 변수 user
                .id(12L)
                .name("Pobi").build();
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));  // findById로 Account를 정상적으로 찾음
        given(accountRepository.countByAccountUser(any()))
                .willReturn(10);   // 응답으로 계좌의 개수=10 을 주었을 때
        //when
        AccountException exception = assertThrows(AccountException.class,   // 해당 logic은 accountException을 던질 것.
                () -> accountService.createAccount(1L, 1000L));

        //then
        assertEquals(ErrorCode.MAX_ACCOUNT_PER_USER_10, exception.getErrorCode());  // 그 exception의 에러코드는 MAX_ACCOUNT_PER_USER_10 일 것.
    }

    @Test
    void deleteAccountSuccess() {
        //given
        AccountUser user = AccountUser.builder()  // 사용될 변수 user
                .id(12L)
                .name("Pobi").build();
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user)); // 위에서 만든 Pobi를 가져옴
        given(accountRepository.findByAccountNumber(anyString()))   // 아무 문자열이나 왔을 때
                .willReturn(Optional.of(Account.builder()
                        .accountUser(user)    // 위의 user와 같은 user여야 함
                        .balance(0L)   // getBalance를 할 때 Null이라서 가져오는 것 자체가 안되므로, 0으로 mocking 하기
                        .accountNumber("1000000012").build()));  // 1000000012 계좌 해지하기
        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);

        //when
        AccountDto accountDto = accountService.deleteAccount(1L, "1234567890");

        //then
        verify(accountRepository, times(1)).save(captor.capture());
        assertEquals(12L, accountDto.getUserId());
        assertEquals("1000000012", captor.getValue().getAccountNumber());  // 해지된 계좌는 1000000012 인지
        assertEquals(AccountStatus.UNREGISTERED, captor.getValue().getAccountStatus());   // 그 계좌의 상태가 UNREGISTERED 인지
    }

    @Test
    @DisplayName("해당 유저 없음 - 계좌 해지 실패")
    void deleteAccount_UserNotFound() {
        //given
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        //when
        AccountException exception = assertThrows(AccountException.class,
                () -> accountService.deleteAccount(1L, "1234567890"));

        //then
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("해당 계좌 없음 - 계좌 해지 실패")
    void deleteAccount_AccountNotFound() {
        //given
        AccountUser user = AccountUser.builder()
                .id(12L)
                .name("Pobi").build();
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.empty());

        //when
        AccountException exception = assertThrows(AccountException.class,
                () -> accountService.deleteAccount(1L, "1234567890"));

        //then
        assertEquals(ErrorCode.ACCOUNT_NOT_FOUND, exception.getErrorCode());   // 그 계좌의 상태가 UNREGISTERED 인지
    }

    @Test
    @DisplayName("계좌 소유주 다름")
    void deleteAccountFailed_userUnMatch() {
        //given
        AccountUser pobi = AccountUser.builder()  // 삭제하고싶은 계좌의 소유주
                .id(12L)
                .name("Pobi").build();
        AccountUser harry = AccountUser.builder()  // 다른 계좌 소유주
                .id(13L)
                .name("Harry").build();
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(pobi)); // pobi
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(harry)    // pobi가 아닌 harry
                        .balance(0L)   // getBalance를 할 때 Null이라서 가져오는 것 자체가 안되므로, 0으로 mocking 하기
                        .accountNumber("1000000012").build()));

        //when
        AccountException exception = assertThrows(AccountException.class,
                () -> accountService.deleteAccount(1L, "1234567890"));

        //then
        assertEquals(ErrorCode.USER_ACCOUNT_UN_MATCH, exception.getErrorCode());  // 에러: 계좌의 소유주 불일치
    }

    @Test
    @DisplayName("해지 계좌는 잔액이 없어야 한다.")
    void deleteAccountFailed_balanceNotEmpty() {
        //given
        AccountUser pobi = AccountUser.builder()
                .id(12L)
                .name("Pobi").build();  // Pobi가 검색되고,
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(pobi));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(pobi)  // pobi가 소유주가 맞는데,
                        .balance(100L)   // 잔액이 100원이 남아있다
                        .accountNumber("1000000012").build()));

        //when
        AccountException exception = assertThrows(AccountException.class,
                () -> accountService.deleteAccount(1L, "1234567890"));

        //then
        assertEquals(ErrorCode.BALANCE_NOT_EMPTY, exception.getErrorCode());  // 잔액 있음 오류
    }

    @Test
    @DisplayName("해지 계좌는 해지할 수 없다.")
    void deleteAccountFailed_alreadyUnregistered() {
        //given
        AccountUser pobi = AccountUser.builder()
                .id(12L)
                .name("Pobi").build();  // Pobi가 검색되고,
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(pobi));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(pobi)  // pobi가 소유주가 맞고,
                        .balance(0L)   //  잔액도 0원인데,
                        .accountStatus(AccountStatus.UNREGISTERED)  // 이미 계좌가 해지된 상태라면
                        .accountNumber("1000000012").build()));

        //when
        AccountException exception = assertThrows(AccountException.class,
                () -> accountService.deleteAccount(1L, "1234567890"));

        //then
        assertEquals(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED, exception.getErrorCode());  //  오류: 이미 해지된 계좌입니다
    }

    @Test
    void successGetAccountsByUserId() {
        //given
        AccountUser pobi = AccountUser.builder()
                .id(12L)
                .name("Pobi").build();  // Pobi가 검색되고,
        List<Account> accounts = Arrays.asList(   // 가상계좌 3개를 만들어서 List에 넣음
                Account.builder()
                        .accountUser(pobi)
                        .accountNumber("1111111111")
                        .balance(1000L)
                        .build(),
                Account.builder()
                        .accountUser(pobi)
                        .accountNumber("2222222222")
                        .balance(2000L)
                        .build(),
                Account.builder()
                        .accountUser(pobi)
                        .accountNumber("3333333333")
                        .balance(3000L)
                        .build()
        );
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(pobi));
        given(accountRepository.findByAccountUser(any()))
                .willReturn(accounts);

        //when
        List<AccountDto> accountDtos = accountService.getAccountsByUserId(1L);

        //then
        assertEquals(3, accountDtos.size());
        assertEquals("1111111111", accountDtos.get(0).getAccountNumber());
        assertEquals(1000, accountDtos.get(0).getBalance());
        assertEquals("2222222222", accountDtos.get(1).getAccountNumber());
        assertEquals(2000, accountDtos.get(1).getBalance());
        assertEquals("3333333333", accountDtos.get(2).getAccountNumber());
        assertEquals(3000, accountDtos.get(2).getBalance());
    }

    @Test
    void failedToGetAccounts() {  // 사용자 id가 없을 때
        //given
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.empty());   // findById로 유저를 조회했을 때 empty() 응답

        //when
        AccountException exception = assertThrows(AccountException.class,
                () -> accountService.getAccountsByUserId(1L));   // 임의의 값을 넣어준다

        //then
        assertEquals(ErrorCode.USER_NOT_FOUND,  exception.getErrorCode());
    }
}

