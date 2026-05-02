package com.banking.service;

import com.banking.dto.TransactionRequest;
import com.banking.dto.TransactionResponse;
import com.banking.entity.Account;
import com.banking.entity.Transaction;
import com.banking.entity.User;
import com.banking.exception.InsufficientFundsException;
import com.banking.exception.ResourceNotFoundException;
import com.banking.repository.AccountRepository;
import com.banking.repository.TransactionRepository;
import com.banking.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock private AccountRepository accountRepository;
    @Mock private TransactionRepository transactionRepository;
    @InjectMocks private TransactionServiceImpl transactionService;

    private Account account;
    private TransactionRequest request;

    @BeforeEach
    void setUp() {
        User user = User.builder().id(1L).name("John").email("john@example.com").build();
        account = Account.builder().id(1L).accountNumber("ACC123")
                .balance(BigDecimal.valueOf(1000)).user(user).build();
        request = new TransactionRequest();
        request.setAmount(BigDecimal.valueOf(100));
        request.setDescription("Test transaction");
    }

    @Test
    void deposit_success() {
        when(accountRepository.findByAccountNumber("ACC123")).thenReturn(Optional.of(account));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> {
            Transaction tx = inv.getArgument(0);
            tx.setId(1L);
            return tx;
        });

        TransactionResponse response = transactionService.deposit("ACC123", request);

        assertThat(account.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(1100));
        assertThat(response.getType()).isEqualTo(Transaction.TransactionType.DEPOSIT);
        verify(accountRepository).save(account);
    }

    @Test
    void withdraw_success() {
        when(accountRepository.findByAccountNumber("ACC123")).thenReturn(Optional.of(account));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> {
            Transaction tx = inv.getArgument(0);
            tx.setId(1L);
            return tx;
        });

        TransactionResponse response = transactionService.withdraw("ACC123", request);

        assertThat(account.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(900));
        assertThat(response.getType()).isEqualTo(Transaction.TransactionType.WITHDRAWAL);
    }

    @Test
    void withdraw_insufficientFunds_throwsException() {
        request.setAmount(BigDecimal.valueOf(2000));
        when(accountRepository.findByAccountNumber("ACC123")).thenReturn(Optional.of(account));

        assertThatThrownBy(() -> transactionService.withdraw("ACC123", request))
                .isInstanceOf(InsufficientFundsException.class)
                .hasMessageContaining("Insufficient balance");
    }

    @Test
    void transfer_success() {
        Account toAccount = Account.builder().id(2L).accountNumber("ACC456")
                .balance(BigDecimal.valueOf(500)).user(account.getUser()).build();
        request.setTargetAccountNumber("ACC456");

        when(accountRepository.findByAccountNumber("ACC123")).thenReturn(Optional.of(account));
        when(accountRepository.findByAccountNumber("ACC456")).thenReturn(Optional.of(toAccount));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> {
            Transaction tx = inv.getArgument(0);
            tx.setId(1L);
            return tx;
        });

        List<TransactionResponse> responses = transactionService.transfer("ACC123", request);

        assertThat(account.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(900));
        assertThat(toAccount.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(600));
        assertThat(responses).hasSize(2);
    }

    @Test
    void transfer_sameAccount_throwsException() {
        request.setTargetAccountNumber("ACC123");

        assertThatThrownBy(() -> transactionService.transfer("ACC123", request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cannot transfer to the same account");
    }

    @Test
    void getTransactionHistory_success() {
        Transaction tx = Transaction.builder().id(1L).type(Transaction.TransactionType.DEPOSIT)
                .amount(BigDecimal.valueOf(100)).timestamp(LocalDateTime.now())
                .account(account).build();
        when(accountRepository.findByAccountNumber("ACC123")).thenReturn(Optional.of(account));
        when(transactionRepository.findByAccountOrderByTimestampDesc(account)).thenReturn(List.of(tx));

        List<TransactionResponse> responses = transactionService.getTransactionHistory("ACC123");

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getAmount()).isEqualByComparingTo(BigDecimal.valueOf(100));
    }
}
