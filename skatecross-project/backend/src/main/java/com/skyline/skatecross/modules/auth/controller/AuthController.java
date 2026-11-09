package com.skyline.skatecross.modules.auth.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.skyline.skatecross.common.security.AuthenticatedUser;
import com.skyline.skatecross.modules.auth.dto.CurrentUserResponse;
import com.skyline.skatecross.modules.auth.dto.LoginRequest;
import com.skyline.skatecross.modules.auth.dto.LoginResponse;
import com.skyline.skatecross.modules.auth.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Login dan profil User menggunakan JWT")
public class AuthController {

    private final AuthService authService;

    public AuthController(
            AuthService authService) {
        this.authService = authService;
    }

    // ==========================================
    // POST /api/auth/login
    // ==========================================

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Login menggunakan email dan password")
    public LoginResponse login(
            @Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    // ==========================================
    // GET /api/auth/me
    // ==========================================

    @GetMapping("/me")
    @Operation(summary = "Get Current User", description = "Mengambil User berdasarkan JWT yang sedang digunakan")
    @SecurityRequirement(name = "bearerAuth")
    public CurrentUserResponse me(
            @AuthenticationPrincipal AuthenticatedUser principal) {

        if (principal == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Belum login");
        }

        return new CurrentUserResponse(
                principal.getId(),
                principal.getFullName(),
                principal.getEmail(),
                principal.getRoleCode());
    }
}