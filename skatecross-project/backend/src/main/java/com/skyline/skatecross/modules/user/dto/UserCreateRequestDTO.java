package com.skyline.skatecross.modules.user.dto;

public class UserCreateRequestDTO {
    
    // Frontend: Dropdown / Select box (pilihan role)
    private Long roleId;
    
    // Frontend: Input type="text"
    private String fullName;
    
    // Frontend: Input type="email"
    private String email;
    
    // Frontend: Input type="password" (Plain text dari user, di-hash di backend)
    private String password;

    // Konstruktor kosong (Wajib ada agar Spring Boot bisa otomatis melakukan mapping dari JSON)
    public UserCreateRequestDTO() {
    }

    // --- Getter & Setter ---

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}