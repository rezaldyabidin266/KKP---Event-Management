package com.skyline.skatecross.modules.auth.service;

import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.skyline.skatecross.common.security.AuthenticatedUser;
import com.skyline.skatecross.common.security.JwtService;

import com.skyline.skatecross.modules.auth.dto.LoginRequest;
import com.skyline.skatecross.modules.auth.dto.LoginResponse;

import com.skyline.skatecross.modules.role.entity.Role;
import com.skyline.skatecross.modules.user.entity.User;
import com.skyline.skatecross.modules.user.repository.UserRepository;

@Service
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // ==========================================
    // LOGIN
    // ==========================================

    @Override
    public LoginResponse login(LoginRequest request) {

        if (request == null ||
                request.getEmail() == null ||
                request.getPassword() == null) {
            throw invalidCredentials();
        }

        String email = request.getEmail()
                .trim()
                .toLowerCase(Locale.ROOT);

        User user = userRepository
                .findByEmailIgnoreCase(email)
                .orElseThrow(this::invalidCredentials);

        if (!isEligible(user) ||
                user.getPasswordHash() == null ||
                !passwordEncoder.matches(
                        request.getPassword(),
                        user.getPasswordHash())) {
            throw invalidCredentials();
        }

        String accessToken = jwtService.generateToken(
                user.getId());

        return new LoginResponse(
                accessToken,
                jwtService.getExpiresInSeconds());
    }

    // ==========================================
    // LOAD CURRENT USER FROM DATABASE
    // ==========================================

    @Override
    public AuthenticatedUser loadActiveUser(
            Long userId) {

        if (userId == null || userId <= 0) {
            return null;
        }

        return userRepository.findById(userId)
                .filter(this::isEligible)
                .map(user -> new AuthenticatedUser(
                        user.getId(),
                        user.getFullName(),
                        user.getEmail(),
                        user.getRole().getCode()))
                .orElse(null);
    }

    // ==========================================
    // VALIDATE ACTIVE USER AND ROLE
    // ==========================================

    private boolean isEligible(User user) {

        Role role = user.getRole();

        return user.isActive()
                && role != null
                && role.isActive()
                && role.getCode() != null
                && !role.getCode().trim().isEmpty();
    }

    // ==========================================
    // INVALID CREDENTIALS
    // ==========================================

    private ResponseStatusException invalidCredentials() {

        return new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Email atau password salah");
    }
}