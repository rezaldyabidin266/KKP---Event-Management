package com.skyline.skatecross.modules.event.dto;

import com.skyline.skatecross.modules.category.dto.CategoryResponseDto;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

public class EventResponseDto {

    private Long id;
    private String eventCode;
    private String name;
    private LocalDate eventDate;
    private OffsetDateTime registrationOpenAt;
    private OffsetDateTime registrationCloseAt;
    private String status;
    private Long createdByUserId;
    private Long nextRegistrationNo;
    private List<CategoryResponseDto> categories;

    public EventResponseDto() {
    }

    // --- Getters & Setters ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEventCode() {
        return eventCode;
    }

    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(Long createdByUserId) {
        this.createdByUserId = createdByUserId;
    }

    public Long getNextRegistrationNo() {
        return nextRegistrationNo;
    }

    public void setNextRegistrationNo(Long nextRegistrationNo) {
        this.nextRegistrationNo = nextRegistrationNo;
    }

    public List<CategoryResponseDto> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryResponseDto> categories) {
        this.categories = categories;
    }
}