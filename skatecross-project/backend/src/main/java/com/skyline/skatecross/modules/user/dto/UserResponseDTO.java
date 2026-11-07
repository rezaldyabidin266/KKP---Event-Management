package com.skyline.skatecross.modules.user.dto;

import java.time.OffsetDateTime;

public class UserResponseDTO {
    
    // Frontend: Digunakan sebagai 'Key' atau ID baris pada Grid/Tabel
    private Long id;
    
    // Frontend: Ditampilkan sebagai kolom teks "Role" di tabel
    private String roleName; 
    
    // Frontend: Ditampilkan di tabel
    private String fullName;
    
    // Frontend: Ditampilkan di tabel
    private String email;
    
    // Frontend: Ditampilkan sebagai badge (Active/Inactive) di tabel
    private boolean active;
    
    // Frontend: Ditampilkan sebagai tanggal registrasi
    private OffsetDateTime createdAt;

    // Konstruktor kosong wajib untuk proses serialisasi JSON
    public UserResponseDTO() {
    }

    // --- Getter & Setter ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
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

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}