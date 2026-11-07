package com.skyline.skatecross.modules.user.dto;

public class ChangePasswordRequestDTO {
    
    // Frontend: Input type="password" (Password saat ini)
    private String oldPassword;
    
    // Frontend: Input type="password" (Password baru)
    private String newPassword;
    
    // Frontend: Input type="password" (Konfirmasi password baru)
    private String confirmNewPassword;

    public ChangePasswordRequestDTO() {
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmNewPassword() {
        return confirmNewPassword;
    }

    public void setConfirmNewPassword(String confirmNewPassword) {
        this.confirmNewPassword = confirmNewPassword;
    }
}