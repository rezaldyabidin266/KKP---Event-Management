package com.skyline.skatecross.modules.health.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HealthServiceImplTest {
    @Test
    void checkReturnsUp() {
        HealthService service = new HealthServiceImpl();

        assertEquals("UP", service.check().getStatus());
        assertEquals("skatecross-api", service.check().getApplication());
    }
}
