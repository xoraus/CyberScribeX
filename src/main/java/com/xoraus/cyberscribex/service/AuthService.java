package com.xoraus.cyberscribex.service;

import com.xoraus.cyberscribex.payload.LoginDto;
import com.xoraus.cyberscribex.payload.RegisterDto;

public interface AuthService {
    String login(LoginDto loginDto);
    String register(RegisterDto registerDto);
}
