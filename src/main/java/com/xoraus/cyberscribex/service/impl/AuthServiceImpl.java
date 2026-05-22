package com.xoraus.cyberscribex.service.impl;

import com.xoraus.cyberscribex.entity.Role;
import com.xoraus.cyberscribex.entity.User;
import com.xoraus.cyberscribex.exception.CyberScribeXAPIException;
import com.xoraus.cyberscribex.payload.JWTAuthResponse;
import com.xoraus.cyberscribex.payload.LoginDto;
import com.xoraus.cyberscribex.payload.RegisterDto;
import com.xoraus.cyberscribex.repository.RoleRepository;
import com.xoraus.cyberscribex.repository.UserRepository;
import com.xoraus.cyberscribex.security.JwtTokenProvider;
import com.xoraus.cyberscribex.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long LOCK_TIME_DURATION_MINUTES = 30;

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${app.jwt.expiration-milliseconds:900000}")
    private long jwtExpiration;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    @Transactional
    public JWTAuthResponse login(LoginDto loginDto) {
        User user = userRepository.findByUsernameOrEmail(
                loginDto.getUsernameOrEmail(), loginDto.getUsernameOrEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        if (!user.isAccountNonLocked()) {
            if (unlockWhenTimeExpired(user)) {
                log.info("Account unlocked for user: {}", user.getUsername());
            } else {
                throw new LockedException("Account is locked");
            }
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getUsernameOrEmail(),
                            loginDto.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            resetFailedAttempts(user);

            String accessToken = jwtTokenProvider.generateToken(authentication);
            String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

            log.info("User logged in successfully: {}", user.getUsername());

            return buildAuthResponse(accessToken, refreshToken, user);

        } catch (BadCredentialsException ex) {
            increaseFailedAttempts(user);
            throw ex;
        }
    }

    @Override
    @Transactional
    public JWTAuthResponse register(RegisterDto registerDto) {
        if (userRepository.existsByUsername(registerDto.getUsername())) {
            throw new CyberScribeXAPIException(HttpStatus.CONFLICT,
                    "Username '" + registerDto.getUsername() + "' is already taken");
        }

        if (userRepository.existsByEmail(registerDto.getEmail())) {
            throw new CyberScribeXAPIException(HttpStatus.CONFLICT,
                    "Email '" + registerDto.getEmail() + "' is already registered");
        }

        User user = User.builder()
                .name(registerDto.getName())
                .username(registerDto.getUsername())
                .email(registerDto.getEmail())
                .password(passwordEncoder.encode(registerDto.getPassword()))
                .accountNonLocked(true)
                .failedAttempt(0)
                .build();

        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new CyberScribeXAPIException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Default role not found. Please contact support."));
        roles.add(userRole);
        user.setRoles(roles);

        User savedUser = userRepository.save(user);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                savedUser.getEmail(), null, roles.stream()
                        .map(role -> new org.springframework.security.core.authority.SimpleGrantedAuthority(role.getName()))
                        .collect(Collectors.toSet())
        );

        String accessToken = jwtTokenProvider.generateToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        log.info("User registered successfully: {}", savedUser.getUsername());

        return buildAuthResponse(accessToken, refreshToken, savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public JWTAuthResponse refreshToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken) || !jwtTokenProvider.isRefreshToken(refreshToken)) {
            throw new CyberScribeXAPIException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }

        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
        User user = userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new CyberScribeXAPIException(HttpStatus.UNAUTHORIZED, "User not found"));

        if (!user.isAccountNonLocked()) {
            throw new CyberScribeXAPIException(HttpStatus.LOCKED, "Account is locked");
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(), null, user.getRoles().stream()
                        .map(role -> new org.springframework.security.core.authority.SimpleGrantedAuthority(role.getName()))
                        .collect(Collectors.toSet())
        );

        String newAccessToken = jwtTokenProvider.generateToken(authentication);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        log.info("Token refreshed for user: {}", user.getUsername());

        return buildAuthResponse(newAccessToken, newRefreshToken, user);
    }

    private void increaseFailedAttempts(User user) {
        int failedAttempts = user.getFailedAttempt() + 1;
        userRepository.updateFailedAttempts(failedAttempts, user.getEmail());

        if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
            lockUser(user);
            log.warn("Account locked due to too many failed attempts: {}", user.getUsername());
            throw new CyberScribeXAPIException(HttpStatus.LOCKED,
                    "Your account has been locked due to " + MAX_FAILED_ATTEMPTS +
                    " failed attempts. It will be unlocked after " + LOCK_TIME_DURATION_MINUTES + " minutes.");
        }
    }

    private void resetFailedAttempts(User user) {
        userRepository.updateFailedAttempts(0, user.getEmail());
    }

    private void lockUser(User user) {
        user.setAccountNonLocked(false);
        user.setLockTime(LocalDateTime.now());
        userRepository.save(user);
    }

    private boolean unlockWhenTimeExpired(User user) {
        if (user.getLockTime() == null) {
            return false;
        }
        LocalDateTime lockTime = user.getLockTime();
        LocalDateTime unlockTime = lockTime.plusMinutes(LOCK_TIME_DURATION_MINUTES);

        if (LocalDateTime.now().isAfter(unlockTime)) {
            user.setAccountNonLocked(true);
            user.setLockTime(null);
            user.setFailedAttempt(0);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    private JWTAuthResponse buildAuthResponse(String accessToken, String refreshToken, User user) {
        return JWTAuthResponse.builder()
                .tokenType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtExpiration)
                .username(user.getUsername())
                .roles(user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet()))
                .build();
    }
}
