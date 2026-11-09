package com.skyline.skatecross.modules.auth.service;

import com.skyline.skatecross.common.security.AuthenticatedUser;
import com.skyline.skatecross.modules.auth.dto.LoginRequest;
import com.skyline.skatecross.modules.auth.dto.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    AuthenticatedUser loadActiveUser(Long userId);
}