package com.skyline.skatecross.modules.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import com.skyline.skatecross.common.security.AuthenticatedUser;
import com.skyline.skatecross.common.security.JwtService;

import com.skyline.skatecross.modules.auth.dto.LoginRequest;
import com.skyline.skatecross.modules.auth.dto.LoginResponse;

import com.skyline.skatecross.modules.role.entity.Role;
import com.skyline.skatecross.modules.user.entity.User;
import com.skyline.skatecross.modules.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    private User user;
    private Role role;

    @BeforeEach
    void setup() {
        user = mock(User.class);
        role = mock(Role.class);
    }

    private void setupActiveUser() {

        when(user.isActive()).thenReturn(true);
        when(user.getRole()).thenReturn(role);

        when(role.isActive()).thenReturn(true);
        when(role.getCode()).thenReturn("SUPER_ADMIN");
    }

    @Test
    void loginSuccess() {

        when(
                userRepository.findByEmailIgnoreCase(
                        "admin@example.com"))
                .thenReturn(Optional.of(user));

        setupActiveUser();

        when(user.getPasswordHash())
                .thenReturn("hashed-password");

        when(
                passwordEncoder.matches(
                        "password123",
                        "hashed-password"))
                .thenReturn(true);

        when(user.getId()).thenReturn(10L);

        when(jwtService.generateToken(10L))
                .thenReturn("jwt-token");

        when(jwtService.getExpiresInSeconds())
                .thenReturn(3600L);

        LoginRequest request = new LoginRequest(
                "ADMIN@EXAMPLE.COM",
                "password123");

        LoginResponse response = authService.login(request);

        assertEquals(
                "jwt-token",
                response.getAccessToken());

        assertEquals(
                "Bearer",
                response.getTokenType());

        assertEquals(
                3600L,
                response.getExpiresIn());
    }

    @Test
    void wrongPasswordShouldReturn401() {

        when(
                userRepository.findByEmailIgnoreCase(
                        "admin@example.com"))
                .thenReturn(Optional.of(user));

        setupActiveUser();

        when(user.getPasswordHash())
                .thenReturn("hashed-password");

        when(
                passwordEncoder.matches(
                        "wrong-password",
                        "hashed-password"))
                .thenReturn(false);

        LoginRequest request = new LoginRequest(
                "admin@example.com",
                "wrong-password");

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authService.login(request));

        assertEquals(
                HttpStatus.UNAUTHORIZED,
                exception.getStatus());

        verify(
                jwtService,
                never()).generateToken(anyLong());
    }

    @Test
    void userNotFoundShouldReturn401() {

        when(
                userRepository.findByEmailIgnoreCase(
                        "missing@example.com"))
                .thenReturn(Optional.empty());

        LoginRequest request = new LoginRequest(
                "missing@example.com",
                "password123");

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authService.login(request));

        assertEquals(
                HttpStatus.UNAUTHORIZED,
                exception.getStatus());
    }

    @Test
    void inactiveUserShouldReturn401() {

        when(
                userRepository.findByEmailIgnoreCase(
                        "admin@example.com"))
                .thenReturn(Optional.of(user));

        // isActive() pada mock bernilai false.

        LoginRequest request = new LoginRequest(
                "admin@example.com",
                "password123");

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authService.login(request));

        assertEquals(
                HttpStatus.UNAUTHORIZED,
                exception.getStatus());
    }

    @Test
    void shouldLoadAuthenticatedUser() {

        when(userRepository.findById(10L))
                .thenReturn(Optional.of(user));

        setupActiveUser();

        when(user.getId()).thenReturn(10L);

        when(user.getEmail())
                .thenReturn("admin@example.com");

        when(user.getFullName())
                .thenReturn("Administrator");

        AuthenticatedUser principal = authService.loadActiveUser(10L);

        assertNotNull(principal);

        assertEquals(
                Long.valueOf(10L),
                principal.getId());

        assertEquals(
                "SUPER_ADMIN",
                principal.getRoleCode());
    }

    @Test
    void missingUserShouldReturnNull() {

        when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        AuthenticatedUser principal = authService.loadActiveUser(99L);

        assertNull(principal);
    }
}