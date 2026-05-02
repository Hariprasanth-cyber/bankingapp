package com.banking.service;

import com.banking.dto.LoginRequest;
import com.banking.dto.LoginResponse;
import com.banking.dto.RegisterRequest;

public interface AuthService {
    String register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
}
