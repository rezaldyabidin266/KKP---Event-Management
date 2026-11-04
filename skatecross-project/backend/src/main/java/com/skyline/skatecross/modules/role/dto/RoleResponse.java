
package com.skyline.skatecross.modules.role.dto;

public class RoleResponse {

    private final Long id;
    private final String code;
    private final String name;
    private final String description;
    private final boolean active;

    public RoleResponse(
            Long id,
            String code,
            String name,
            String description,
            boolean active) {

        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return active;
    }
}
