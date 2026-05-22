package com.xoraus.cyberscribex.service;

import com.xoraus.cyberscribex.entity.Role;
import com.xoraus.cyberscribex.entity.User;
import com.xoraus.cyberscribex.exception.CyberScribeXAPIException;
import com.xoraus.cyberscribex.payload.JWTAuthResponse;
import com.xoraus.cyberscribex.payload.LoginDto;
import com.xoraus.cyberscribex.payload.RegisterDto;
import com.xoraus.cyberscribex.repository.RoleRepository;
import com.xoraus.cyberscribex.repository.UserRepository;
import com.xoraus.cyberscribex.security.JwtTokenProvider;
import com.xoraus.cyberscribex.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("Auth Service Unit Tests")
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthServiceImpl authService;

    private LoginDto loginDto;
    private RegisterDto registerDto;
    private User user;
    private Role role;

    @BeforeEach
    void setUp() {
        loginDto = LoginDto.builder()
                .usernameOrEmail("johndoe")
                .password("password123")
                .build();

        registerDto = RegisterDto.builder()
                .name("John Doe")
                .username("johndoe")
                .email("john@example.com")
                .password("SecureP@ss123")
                .build();

        role = Role.builder().id(1L).name("ROLE_USER").build();

        user = User.builder()
                .id(1L)
                .name("John Doe")
                .username("johndoe")
                .email("john@example.com")
                .password("encoded_password")
                .roles(Set.of(role))
                .accountNonLocked(true)
                .build();
    }

    @Test
    @DisplayName("Should login successfully")
    void shouldLoginSuccessfully() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "johndoe", null, Set.of(() -> "ROLE_USER")
        );

        given(userRepository.findByUsernameOrEmail("johndoe", "johndoe"))
                .willReturn(Optional.of(user));
        given(authenticationManager.authenticate(any()))
                .willReturn(authentication);
        given(jwtTokenProvider.generateToken(authentication))
                .willReturn("access_token");
        given(jwtTokenProvider.generateRefreshToken(authentication))
                .willReturn("refresh_token");

        JWTAuthResponse response = authService.login(loginDto);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("access_token");
        assertThat(response.getUsername()).isEqualTo("johndoe");
    }

    @Test
    @DisplayName("Should throw exception for invalid credentials")
    void shouldThrowExceptionForInvalidCredentials() {
        given(userRepository.findByUsernameOrEmail("johndoe", "johndoe"))
                .willReturn(Optional.of(user));
        given(authenticationManager.authenticate(any()))
                .willThrow(new BadCredentialsException("Invalid credentials"));

        assertThatThrownBy(() -> authService.login(loginDto))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    @DisplayName("Should register user successfully")
    void shouldRegisterSuccessfully() {
        given(userRepository.existsByUsername("johndoe")).willReturn(false);
        given(userRepository.existsByEmail("john@example.com")).willReturn(false);
        given(passwordEncoder.encode("SecureP@ss123")).willReturn("encoded_password");
        given(roleRepository.findByName("ROLE_USER")).willReturn(Optional.of(role));
        given(userRepository.save(any(User.class))).willReturn(user);
        given(jwtTokenProvider.generateToken(any())).willReturn("access_token");
        given(jwtTokenProvider.generateRefreshToken(any())).willReturn("refresh_token");

        JWTAuthResponse response = authService.register(registerDto);

        assertThat(response).isNotNull();
        assertThat(response.getUsername()).isEqualTo("johndoe");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when username exists")
    void shouldThrowExceptionWhenUsernameExists() {
        given(userRepository.existsByUsername("johndoe")).willReturn(true);

        assertThatThrownBy(() -> authService.register(registerDto))
                .isInstanceOf(CyberScribeXAPIException.class)
                .hasMessageContaining("already taken");
    }
}
