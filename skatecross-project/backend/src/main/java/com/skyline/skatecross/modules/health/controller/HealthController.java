package com.skyline.skatecross.modules.health.controller;

import com.skyline.skatecross.modules.health.dto.HealthResponse;
import com.skyline.skatecross.modules.health.service.HealthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
public class HealthController {
    private final HealthService healthService;

    public HealthController(HealthService healthService) {
        this.healthService = healthService;
    }

    @GetMapping
    public HealthResponse check() {
        return healthService.check();
    }
}
