package com.banking.service.impl;

import com.banking.dto.AccountResponse;
import com.banking.entity.Account;
import com.banking.entity.User;
import com.banking.exception.ResourceNotFoundException;
import com.banking.repository.AccountRepository;
import com.banking.repository.UserRepository;
import com.banking.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Override
    public AccountResponse createAccount(String email) {
        User user = findUserByEmail(email);
        String accountNumber = generateUniqueAccountNumber();
        Account account = Account.builder()
                .accountNumber(accountNumber)
                .balance(BigDecimal.ZERO)
                .user(user)
                .build();
        return toResponse(accountRepository.save(account));
    }

    @Override
    public AccountResponse getAccountByNumber(String accountNumber) {
        return toResponse(findAccountByNumber(accountNumber));
    }

    @Override
    public List<AccountResponse> getAccountsByUser(String email) {
        User user = findUserByEmail(email);
        return accountRepository.findByUser(user).stream().map(this::toResponse).toList();
    }

    private String generateUniqueAccountNumber() {
        String number;
        do {
            number = "ACC" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
        } while (accountRepository.existsByAccountNumber(number));
        return number;
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }

    private Account findAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + accountNumber));
    }

    private AccountResponse toResponse(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getBalance(),
                account.getUser().getName(),
                account.getUser().getEmail()
        );
    }
}
