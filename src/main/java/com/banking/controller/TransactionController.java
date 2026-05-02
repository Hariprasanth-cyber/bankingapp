package com.banking.controller;

import com.banking.dto.TransactionRequest;
import com.banking.dto.TransactionResponse;
import com.banking.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "Banking transaction APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/{accountNumber}/deposit")
    @Operation(summary = "Deposit money into an account")
    public ResponseEntity<TransactionResponse> deposit(
            @PathVariable String accountNumber,
            @Valid @RequestBody TransactionRequest request) {
        return ResponseEntity.ok(transactionService.deposit(accountNumber, request));
    }

    @PostMapping("/{accountNumber}/withdraw")
    @Operation(summary = "Withdraw money from an account")
    public ResponseEntity<TransactionResponse> withdraw(
            @PathVariable String accountNumber,
            @Valid @RequestBody TransactionRequest request) {
        return ResponseEntity.ok(transactionService.withdraw(accountNumber, request));
    }

    @PostMapping("/{accountNumber}/transfer")
    @Operation(summary = "Transfer money between accounts")
    public ResponseEntity<List<TransactionResponse>> transfer(
            @PathVariable String accountNumber,
            @Valid @RequestBody TransactionRequest request) {
        return ResponseEntity.ok(transactionService.transfer(accountNumber, request));
    }

    @GetMapping("/{accountNumber}/history")
    @Operation(summary = "Get transaction history for an account")
    public ResponseEntity<List<TransactionResponse>> history(@PathVariable String accountNumber) {
        return ResponseEntity.ok(transactionService.getTransactionHistory(accountNumber));
    }
}
