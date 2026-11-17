package com.skyline.skatecross.modules.event.dto;

import com.skyline.skatecross.modules.category.dto.EventCategoryCreateRequestDto;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

public class EventCreateRequestDto {

    @NotBlank(message = "Nama event tidak boleh kosong")
    private String name; // Nama event

    @NotNull(message = "PIC event harus diisi")
    private Long picUserId; // PIC (created_by_user_id)

    @NotNull(message = "Tanggal pelaksanaan event harus diisi")
    private LocalDate eventDate; // Tanggal pelaksanaan

    @NotNull(message = "Tanggal mulai pendaftaran harus diisi")
    private OffsetDateTime registrationOpenAt; // Tanggal mulai pendaftaran

    @NotNull(message = "Tanggal akhir pendaftaran harus diisi")
    private OffsetDateTime registrationCloseAt; // Tanggal akhir pendaftaran

    @NotEmpty(message = "Event minimal harus memiliki 1 kategori lomba")
    @Valid // Agar validasi di dalam objek kategori ikut tereksekusi secara otomatis
    private List<EventCategoryCreateRequestDto> categories; // Daftar kategori lomba

    // Constructors, Getters, dan Setters
    public EventCreateRequestDto() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getPicUserId() { return picUserId; }
    public void setPicUserId(Long picUserId) { this.picUserId = picUserId; }

    public LocalDate getEventDate() { return eventDate; }
    public void setEventDate(LocalDate eventDate) { this.eventDate = eventDate; }

    public OffsetDateTime getRegistrationOpenAt() { return registrationOpenAt; }
    public void setRegistrationOpenAt(OffsetDateTime registrationOpenAt) { this.registrationOpenAt = registrationOpenAt; }

    public OffsetDateTime getRegistrationCloseAt() { return registrationCloseAt; }
    public void setRegistrationCloseAt(OffsetDateTime registrationCloseAt) { this.registrationCloseAt = registrationCloseAt; }

    public List<EventCategoryCreateRequestDto> getCategories() { return categories; }
    public void setCategories(List<EventCategoryCreateRequestDto> categories) { this.categories = categories; }
}