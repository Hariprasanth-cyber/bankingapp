package com.banking.service;

import com.banking.dto.AccountResponse;

import java.util.List;

public interface AccountService {
    AccountResponse createAccount(String email);
    AccountResponse getAccountByNumber(String accountNumber);
    List<AccountResponse> getAccountsByUser(String email);
}
