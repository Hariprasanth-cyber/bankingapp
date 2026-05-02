package com.banking.service;

import com.banking.dto.AccountResponse;
import com.banking.entity.Account;
import com.banking.entity.User;
import com.banking.exception.ResourceNotFoundException;
import com.banking.repository.AccountRepository;
import com.banking.repository.UserRepository;
import com.banking.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock private AccountRepository accountRepository;
    @Mock private UserRepository userRepository;
    @InjectMocks private AccountServiceImpl accountService;

    private User user;
    private Account account;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).name("John Doe").email("john@example.com")
                .password("encoded").role(User.Role.USER).build();
        account = Account.builder().id(1L).accountNumber("ACC1234567890")
                .balance(BigDecimal.valueOf(1000)).user(user).build();
    }

    @Test
    void createAccount_success() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(accountRepository.existsByAccountNumber(anyString())).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        AccountResponse response = accountService.createAccount(user.getEmail());

        assertThat(response.getAccountNumber()).isEqualTo("ACC1234567890");
        assertThat(response.getOwnerEmail()).isEqualTo(user.getEmail());
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void createAccount_userNotFound_throwsException() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.createAccount("unknown@example.com"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void getAccountByNumber_success() {
        when(accountRepository.findByAccountNumber(account.getAccountNumber())).thenReturn(Optional.of(account));

        AccountResponse response = accountService.getAccountByNumber(account.getAccountNumber());

        assertThat(response.getAccountNumber()).isEqualTo(account.getAccountNumber());
        assertThat(response.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(1000));
    }

    @Test
    void getAccountByNumber_notFound_throwsException() {
        when(accountRepository.findByAccountNumber("INVALID")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.getAccountByNumber("INVALID"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Account not found");
    }

    @Test
    void getAccountsByUser_success() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(accountRepository.findByUser(user)).thenReturn(List.of(account));

        List<AccountResponse> responses = accountService.getAccountsByUser(user.getEmail());

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getOwnerEmail()).isEqualTo(user.getEmail());
    }
}
