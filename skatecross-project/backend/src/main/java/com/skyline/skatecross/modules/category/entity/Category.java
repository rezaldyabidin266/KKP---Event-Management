package com.skyline.skatecross.modules.category.entity;

import com.skyline.skatecross.modules.event.entity.Event;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "category_code", length = 40, nullable = false)
    private String categoryCode;

    @Column(name = "name", length = 160, nullable = false)
    private String name;

    @Column(name = "min_age")
    private Integer minAge;

    @Column(name = "max_age")
    private Integer maxAge;

    @Column(name = "gender", length = 12, nullable = false)
    private String gender;

    @Column(name = "fee", precision = 14, scale = 2, nullable = false)
    private BigDecimal fee;

    @Column(name = "quota", nullable = false)
    private Integer quota;

    @Column(name = "cup_size", nullable = false)
    private Integer cupSize;

    @Column(name = "pre_race_window_minutes", nullable = false)
    private Integer preRaceWindowMinutes;

    @Column(name = "registration_open_at")
    private OffsetDateTime registrationOpenAt;

    @Column(name = "registration_close_at")
    private OffsetDateTime registrationCloseAt;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    public Category() {
    }

    @PrePersist
    protected void onCreate() {
        OffsetDateTime now = OffsetDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.isActive == null) {
            this.isActive = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    // --- Getters & Setters ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

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

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}