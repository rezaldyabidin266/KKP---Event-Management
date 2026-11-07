package com.skyline.skatecross.modules.user.dto;

public class UserUpdateRequestDTO {
    
    // Frontend: Dropdown / Select box (terpilih berdasarkan data saat ini)
    private Long roleId;
    
    // Frontend: Input type="text"
    private String fullName;
    
    // Frontend: Input type="email"
    private String email;
    
    // Frontend: Checkbox / Toggle switch (Active / Inactive)
    private boolean active;

    // Konstruktor kosong wajib untuk proses deserialisasi JSON
    public UserUpdateRequestDTO() {
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}