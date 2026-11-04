package com.skyline.skatecross.modules.health.service;

import com.skyline.skatecross.modules.health.dto.HealthResponse;
import org.springframework.stereotype.Service;

@Service
public class HealthServiceImpl implements HealthService {
    @Override
    public HealthResponse check() {
        return new HealthResponse("UP", "skatecross-api");
    }
}
