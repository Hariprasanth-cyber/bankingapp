package com.banking.service.impl;

import com.banking.dto.TransactionRequest;
import com.banking.dto.TransactionResponse;
import com.banking.entity.Account;
import com.banking.entity.Transaction;
import com.banking.entity.Transaction.TransactionType;
import com.banking.exception.InsufficientFundsException;
import com.banking.exception.ResourceNotFoundException;
import com.banking.repository.AccountRepository;
import com.banking.repository.TransactionRepository;
import com.banking.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Override
    @Transactional
    public TransactionResponse deposit(String accountNumber, TransactionRequest request) {
        Account account = findAccount(accountNumber);
        account.setBalance(account.getBalance().add(request.getAmount()));
        accountRepository.save(account);
        Transaction tx = saveTransaction(account, TransactionType.DEPOSIT, request);
        return toResponse(tx);
    }

    @Override
    @Transactional
    public TransactionResponse withdraw(String accountNumber, TransactionRequest request) {
        Account account = findAccount(accountNumber);
        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientFundsException("Insufficient balance in account: " + accountNumber);
        }
        account.setBalance(account.getBalance().subtract(request.getAmount()));
        accountRepository.save(account);
        Transaction tx = saveTransaction(account, TransactionType.WITHDRAWAL, request);
        return toResponse(tx);
    }

    @Override
    @Transactional
    public List<TransactionResponse> transfer(String fromAccountNumber, TransactionRequest request) {
        if (fromAccountNumber.equals(request.getTargetAccountNumber())) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }
        Account from = findAccount(fromAccountNumber);
        Account to = findAccount(request.getTargetAccountNumber());

        if (from.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientFundsException("Insufficient balance in account: " + fromAccountNumber);
        }

        from.setBalance(from.getBalance().subtract(request.getAmount()));
        to.setBalance(to.getBalance().add(request.getAmount()));
        accountRepository.save(from);
        accountRepository.save(to);

        Transaction txOut = saveTransaction(from, TransactionType.TRANSFER_OUT, request);
        Transaction txIn = saveTransaction(to, TransactionType.TRANSFER_IN, request);
        return List.of(toResponse(txOut), toResponse(txIn));
    }

    @Override
    public List<TransactionResponse> getTransactionHistory(String accountNumber) {
        Account account = findAccount(accountNumber);
        return transactionRepository.findByAccountOrderByTimestampDesc(account)
                .stream().map(this::toResponse).toList();
    }

    private Account findAccount(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + accountNumber));
    }

    private Transaction saveTransaction(Account account, TransactionType type, TransactionRequest request) {
        return transactionRepository.save(Transaction.builder()
                .account(account)
                .type(type)
                .amount(request.getAmount())
                .description(request.getDescription())
                .timestamp(LocalDateTime.now())
                .build());
    }

    private TransactionResponse toResponse(Transaction tx) {
        return new TransactionResponse(
                tx.getId(),
                tx.getType(),
                tx.getAmount(),
                tx.getTimestamp(),
                tx.getDescription(),
                tx.getAccount().getAccountNumber()
        );
    }
}
