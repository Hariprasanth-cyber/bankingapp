package com.banking.service;

import com.banking.dto.TransactionRequest;
import com.banking.dto.TransactionResponse;

import java.util.List;

public interface TransactionService {
    TransactionResponse deposit(String accountNumber, TransactionRequest request);
    TransactionResponse withdraw(String accountNumber, TransactionRequest request);
    List<TransactionResponse> transfer(String fromAccountNumber, TransactionRequest request);
    List<TransactionResponse> getTransactionHistory(String accountNumber);
}
