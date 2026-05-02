package com.banking.controller;

import com.banking.dto.TransactionRequest;
import com.banking.service.AccountService;
import com.banking.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final AccountService accountService;
    private final TransactionService transactionService;

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping("/deposit")
    public String depositPage() {
        return "deposit";
    }

    @GetMapping("/withdraw")
    public String withdrawPage() {
        return "withdraw";
    }

    @GetMapping("/transfer")
    public String transferPage() {
        return "transfer";
    }

    @GetMapping("/transactions/{accountNumber}")
    public String transactionsPage(@PathVariable String accountNumber, Model model) {
        model.addAttribute("accountNumber", accountNumber);
        return "transactions";
    }

    @PostMapping("/web/deposit")
    public String doDeposit(@RequestParam String accountNumber,
                            @RequestParam java.math.BigDecimal amount,
                            @AuthenticationPrincipal UserDetails userDetails) {
        TransactionRequest req = new TransactionRequest();
        req.setAmount(amount);
        req.setDescription("Web deposit");
        transactionService.deposit(accountNumber, req);
        return "redirect:/dashboard";
    }

    @PostMapping("/web/withdraw")
    public String doWithdraw(@RequestParam String accountNumber,
                             @RequestParam java.math.BigDecimal amount,
                             @AuthenticationPrincipal UserDetails userDetails) {
        TransactionRequest req = new TransactionRequest();
        req.setAmount(amount);
        req.setDescription("Web withdrawal");
        transactionService.withdraw(accountNumber, req);
        return "redirect:/dashboard";
    }

    @PostMapping("/web/transfer")
    public String doTransfer(@RequestParam String fromAccount,
                             @RequestParam String toAccount,
                             @RequestParam java.math.BigDecimal amount) {
        TransactionRequest req = new TransactionRequest();
        req.setAmount(amount);
        req.setTargetAccountNumber(toAccount);
        req.setDescription("Web transfer");
        transactionService.transfer(fromAccount, req);
        return "redirect:/dashboard";
    }

    @PostMapping("/web/create-account")
    public String createAccount(@AuthenticationPrincipal UserDetails userDetails) {
        accountService.createAccount(userDetails.getUsername());
        return "redirect:/dashboard";
    }
}
