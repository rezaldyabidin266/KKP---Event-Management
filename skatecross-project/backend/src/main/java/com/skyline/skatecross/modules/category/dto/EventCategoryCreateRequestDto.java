package com.skyline.skatecross.modules.category.dto;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class EventCategoryCreateRequestDto {

    @NotBlank(message = "Nama kategori tidak boleh kosong")
    @Size(max = 160, message = "Nama kategori maksimal 160 karakter")
    private String name;

    @NotNull(message = "Usia minimal tidak boleh kosong")
    @Min(value = 0, message = "Usia minimal tidak boleh negatif")
    private Integer minAge;

    @NotNull(message = "Usia maksimal tidak boleh kosong")
    @Min(value = 0, message = "Usia maksimal tidak boleh negatif")
    private Integer maxAge;

    @NotBlank(message = "Gender (putra/putri) tidak boleh kosong")
    @Pattern(regexp = "^(Putra|Putri|PUTRA|PUTRI)$", message = "Gender harus 'Putra' atau 'Putri'")
    private String gender;

    @NotNull(message = "Biaya pendaftaran tidak boleh kosong")
    @DecimalMin(value = "0.0", inclusive = true, message = "Biaya tidak boleh minus")
    private BigDecimal fee;

    @NotNull(message = "Kuota tidak boleh kosong")
    @Min(value = 1, message = "Kuota minimal 1")
    private Integer quota;

    @NotNull(message = "Ukuran cup harus dipilih")
    private Integer cupSize; // Ukuran Cup (32 / 16 / 8 / 4)

    @NotNull(message = "Waktu tunggu pre-race tidak boleh kosong")
    @Min(value = 0, message = "Waktu tunggu pre-race tidak boleh negatif")
    private Integer preRaceWindowMinutes;

    // Opsional: jika null, otomatis mengikuti jadwal pendaftaran event
    private OffsetDateTime registrationOpenAt;

    private OffsetDateTime registrationCloseAt;

    @NotNull(message = "PJ Kategori harus diisi")
    private Long pjUserId;

    public EventCategoryCreateRequestDto() {}

    // --- Getters & Setters ---
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getMinAge() { return minAge; }
    public void setMinAge(Integer minAge) { this.minAge = minAge; }

    public Integer getMaxAge() { return maxAge; }
    public void setMaxAge(Integer maxAge) { this.maxAge = maxAge; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public BigDecimal getFee() { return fee; }
    public void setFee(BigDecimal fee) { this.fee = fee; }

    public Integer getQuota() { return quota; }
    public void setQuota(Integer quota) { this.quota = quota; }

    public Integer getCupSize() { return cupSize; }
    public void setCupSize(Integer cupSize) { this.cupSize = cupSize; }

    public Integer getPreRaceWindowMinutes() { return preRaceWindowMinutes; }
    public void setPreRaceWindowMinutes(Integer preRaceWindowMinutes) { this.preRaceWindowMinutes = preRaceWindowMinutes; }

    public OffsetDateTime getRegistrationOpenAt() { return registrationOpenAt; }
    public void setRegistrationOpenAt(OffsetDateTime registrationOpenAt) { this.registrationOpenAt = registrationOpenAt; }

    public OffsetDateTime getRegistrationCloseAt() { return registrationCloseAt; }
    public void setRegistrationCloseAt(OffsetDateTime registrationCloseAt) { this.registrationCloseAt = registrationCloseAt; }

    public Long getPjUserId() { return pjUserId; }
    public void setPjUserId(Long pjUserId) { this.pjUserId = pjUserId; }
}