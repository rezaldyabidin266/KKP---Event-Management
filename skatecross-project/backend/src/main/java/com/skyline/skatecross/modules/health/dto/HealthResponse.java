package com.skyline.skatecross.modules.health.dto;

public class HealthResponse {
    private final String status;
    private final String application;

    public HealthResponse(String status, String application) {
        this.status = status;
        this.application = application;
    }

    public String getStatus() {
        return status;
    }

    public String getApplication() {
        return application;
    }
}
