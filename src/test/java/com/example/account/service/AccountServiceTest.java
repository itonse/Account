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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
                .willReturn(Optional.of(user)); // 리턴하는 데이터는 Optional 타입의 accountUser가 생성 될 것
        given(accountRepository.findFirstByOrderByIdDesc())
                .willReturn(Optional.of(Account.builder()
                                .accountUser(user)   // 하위에있는 accountUser 담기
                                .accountNumber("1000000012").build()));  // 현재까지 저장된 가장 마지막 계좌번호는 12

        given(accountRepository.save(any()))   // 새로 만들어지는 Account는 여기서 응답으로 줌
                .willReturn(Account.builder()
                        .accountUser(user)
                        .accountNumber("1000000013").build());  // 위에서 12번을 주었으니 13번으로 저장될 것
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
    void createFirstAccountSuccess() {   // findById, findFirstByOrderByIdDesc, save에 대한 Mocking이 모두 되어있어야만 함.
        //given
        AccountUser user = AccountUser.builder()  // 사용될 변수 user
                .id(15L)
                .name("Pobi").build();
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user)); // 리턴하는 데이터는 Optional 타입의 accountUser가 생성 될 것
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
    void createAccount_UserNotFound() {   // findById, findFirstByOrderByIdDesc, save에 대한 Mocking이 모두 되어있어야만 함.
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
    void cerateAccount_maxAccountIs10() {
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
    @DisplayName("계좌 조회 성공")  // 로 표시됨
    void testXXX() {
        //given
        given(accountRepository.findById(anyLong()))  // 목
                .willReturn(Optional.of(Account.builder()
                        .accountStatus(AccountStatus.UNREGISTERED)
                        .accountNumber("65789")
                        .build()));

        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);  // Long타입의 박스 생성

        //when
        Account account = accountService.getAccount(4555L);

        //then
         // 서비스를 테스트하는 다양한 방법들 (verify, assert 등)
        verify(accountRepository, times(1)).findById(captor.capture()); // 캡처가 값을 가로챔
         // accountRepository가 findById를 1회 들렸는지 검증
        assertEquals(4555L, captor.getValue()); // 캡처 값 검증
        assertNotEquals(6555L, captor.getValue()); // 캡처 값 검증
        verify(accountRepository, times(0)).save(any());
         // accountRepository가 save(any() 를 한 번도 호출 안했는지 검증
        assertEquals("65789", account.getAccountNumber());
        assertEquals(AccountStatus.UNREGISTERED, account.getAccountStatus());
    }

    @Test
    @DisplayName("계좌 조회 실패 - 음수로 조회")  // 로 표시됨
    void testFailedToSearchAccount() {
        //given
        //when
        RuntimeException exception = assertThrows(RuntimeException.class, () -> // () ->: 런타임 익셉션이 터지는 동작은
                accountService.getAccount(-10L));// 이 동작이다.

        //then
        assertEquals("Minus", exception.getMessage());
    }

    @Test
    @DisplayName("Test 이름 변경")
    void testGetAccount() {  // Jnit 프레임워크가 실행시킴
        //given
        given(accountRepository.findById(anyLong()))  // 목
                .willReturn(Optional.of(Account.builder()
                        .accountStatus(AccountStatus.UNREGISTERED)
                        .accountNumber("65789")
                        .build()));

        //when
        Account account = accountService.getAccount(4555L);

        //then
        assertEquals("65789", account.getAccountNumber());
        assertEquals(AccountStatus.UNREGISTERED, account.getAccountStatus());
    }

    @Test
    void testGetAccount2() {  // Junit 프레임워크가 실행시킴
        //given
        given(accountRepository.findById(anyLong()))  // 목
                .willReturn(Optional.of(Account.builder()
                        .accountStatus(AccountStatus.UNREGISTERED)
                        .accountNumber("65789")
                        .build()));

        //when
        Account account = accountService.getAccount(4555L);

        //then
        assertEquals("65789", account.getAccountNumber());
        assertEquals(AccountStatus.UNREGISTERED, account.getAccountStatus());
    }

}

