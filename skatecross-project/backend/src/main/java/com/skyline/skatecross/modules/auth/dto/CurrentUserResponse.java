package com.skyline.skatecross.modules.auth.dto;

public class CurrentUserResponse {

    private final Long id;
    private final String fullName;
    private final String email;
    private final String roleCode;

    public CurrentUserResponse(
            Long id,
            String fullName,
            String email,
            String roleCode) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.roleCode = roleCode;
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getRoleCode() {
        return roleCode;
    }
}