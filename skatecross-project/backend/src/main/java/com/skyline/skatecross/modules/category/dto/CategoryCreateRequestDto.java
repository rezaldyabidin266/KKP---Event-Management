package com.skyline.skatecross.modules.category.dto;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class CategoryCreateRequestDto {

    @NotBlank(message = "Nama kategori wajib diisi")
    @Size(max = 160, message = "Nama kategori maksimal 160 karakter")
    private String name;

    @NotNull(message = "Usia minimal wajib diisi")
    @Min(value = 0, message = "Usia minimal tidak boleh negatif")
    private Integer minAge;

    @NotNull(message = "Usia maksimal wajib diisi")
    @Min(value = 0, message = "Usia maksimal tidak boleh negatif")
    private Integer maxAge;

    @NotBlank(message = "Gender (Putra/Putri) wajib diisi")
    @Pattern(regexp = "^(Putra|Putri|PUTRA|PUTRI)$", message = "Gender harus 'Putra' atau 'Putri'")
    private String gender;

    @NotNull(message = "Biaya pendaftaran wajib diisi")
    @DecimalMin(value = "0.0", inclusive = true, message = "Biaya tidak boleh kurang dari 0")
    private BigDecimal fee;

    @NotNull(message = "Kuota peserta wajib diisi")
    @Min(value = 1, message = "Kuota minimal 1 peserta")
    private Integer quota;

    @NotNull(message = "Ukuran Cup (32/16/8/4) wajib diisi")
    private Integer cupSize;

    @NotNull(message = "Waktu tunggu pre-race (menit) wajib diisi")
    @Min(value = 0, message = "Waktu tunggu pre-race tidak boleh negatif")
    private Integer preRaceWindowMinutes;

    private OffsetDateTime registrationOpenAt;

    private OffsetDateTime registrationCloseAt;

    @NotNull(message = "PJ Kategori wajib dipilih")
    private Long pjUserId;

    // --- Getters & Setters ---
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMinAge() {
        return minAge;
    }

    public void setMinAge(Integer minAge) {
        this.minAge = minAge;
    }

    public Integer getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(Integer maxAge) {
        this.maxAge = maxAge;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public Integer getQuota() {
        return quota;
    }

    public void setQuota(Integer quota) {
        this.quota = quota;
    }

    public Integer getCupSize() {
        return cupSize;
    }

    public void setCupSize(Integer cupSize) {
        this.cupSize = cupSize;
    }

    public Integer getPreRaceWindowMinutes() {
        return preRaceWindowMinutes;
    }

    public void setPreRaceWindowMinutes(Integer preRaceWindowMinutes) {
        this.preRaceWindowMinutes = preRaceWindowMinutes;
    }

    public OffsetDateTime getRegistrationOpenAt() {
        return registrationOpenAt;
    }

    public void setRegistrationOpenAt(OffsetDateTime registrationOpenAt) {
        this.registrationOpenAt = registrationOpenAt;
    }

    public OffsetDateTime getRegistrationCloseAt() {
        return registrationCloseAt;
    }

    public void setRegistrationCloseAt(OffsetDateTime registrationCloseAt) {
        this.registrationCloseAt = registrationCloseAt;
    }

    public Long getPjUserId() {
        return pjUserId;
    }

    public void setPjUserId(Long pjUserId) {
        this.pjUserId = pjUserId;
    }
}