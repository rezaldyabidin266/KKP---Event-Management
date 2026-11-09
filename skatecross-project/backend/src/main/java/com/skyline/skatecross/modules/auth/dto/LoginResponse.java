package com.skyline.skatecross.modules.auth.dto;

public class LoginResponse {

    private final String accessToken;
    private final String tokenType;
    private final long expiresIn;

    public LoginResponse(
            String accessToken,
            long expiresIn) {
        this.accessToken = accessToken;
        this.tokenType = "Bearer";
        this.expiresIn = expiresIn;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public long getExpiresIn() {
        return expiresIn;
    }
}