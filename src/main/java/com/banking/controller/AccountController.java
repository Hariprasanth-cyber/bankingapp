package com.banking.controller;

import com.banking.dto.AccountResponse;
import com.banking.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Tag(name = "Accounts", description = "Account management APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    @Operation(summary = "Create a new bank account for the authenticated user")
    public ResponseEntity<AccountResponse> createAccount(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(accountService.createAccount(userDetails.getUsername()));
    }

    @GetMapping
    @Operation(summary = "Get all accounts for the authenticated user")
    public ResponseEntity<List<AccountResponse>> getMyAccounts(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(accountService.getAccountsByUser(userDetails.getUsername()));
    }

    @GetMapping("/{accountNumber}")
    @Operation(summary = "Get account details by account number")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable String accountNumber) {
        return ResponseEntity.ok(accountService.getAccountByNumber(accountNumber));
    }
}
