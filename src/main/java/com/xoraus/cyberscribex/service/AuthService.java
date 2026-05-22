package com.xoraus.cyberscribex.service;

import com.xoraus.cyberscribex.payload.JWTAuthResponse;
import com.xoraus.cyberscribex.payload.LoginDto;
import com.xoraus.cyberscribex.payload.RegisterDto;

public interface AuthService {
    JWTAuthResponse login(LoginDto loginDto);
    JWTAuthResponse register(RegisterDto registerDto);
    JWTAuthResponse refreshToken(String refreshToken);
}
