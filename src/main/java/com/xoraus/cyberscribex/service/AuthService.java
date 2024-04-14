package com.xoraus.cyberscribex.service;

import com.xoraus.cyberscribex.payload.LoginDto;

public interface AuthService {
    String login(LoginDto loginDto);
}
