
package com.skyline.skatecross.modules.role.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.skyline.skatecross.modules.role.dto.RoleRequest;
import com.skyline.skatecross.modules.role.dto.RoleResponse;
import com.skyline.skatecross.modules.role.service.RoleService;

@ExtendWith(MockitoExtension.class)
class RoleControllerTest {

    @Mock
    private RoleService roleService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();

        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(
                        new RoleController(roleService))
                .setValidator(validator)
                .build();
    }

    @Test
    void findAll_shouldReturn200() throws Exception {

        when(roleService.findAll())
                .thenReturn(Arrays.asList(
                        response(
                                1L,
                                "SUPER_ADMIN",
                                "Super Admin",
                                true),
                        response(
                                2L,
                                "EVENT_STAFF",
                                "Panitia Event",
                                true)));

        mockMvc.perform(
                get("/api/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code")
                        .value("SUPER_ADMIN"))
                .andExpect(jsonPath("$[1].code")
                        .value("EVENT_STAFF"));
    }

    @Test
    void findById_shouldReturn200() throws Exception {

        when(roleService.findById(1L))
                .thenReturn(
                        response(
                                1L,
                                "SUPER_ADMIN",
                                "Super Admin",
                                true));

        mockMvc.perform(
                get("/api/roles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.code")
                        .value("SUPER_ADMIN"));
    }

    @Test
    void findById_shouldReturn404() throws Exception {

        when(roleService.findById(99L))
                .thenThrow(
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Role tidak ditemukan"));

        mockMvc.perform(
                get("/api/roles/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_shouldReturn201() throws Exception {

        when(roleService.create(any(RoleRequest.class)))
                .thenReturn(
                        response(
                                2L,
                                "EVENT_STAFF",
                                "Panitia Event",
                                true));

        String json = "{"
                + "\"code\":\"EVENT_STAFF\","
                + "\"name\":\"Panitia Event\","
                + "\"description\":\"Petugas Event\""
                + "}";

        mockMvc.perform(
                post("/api/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.code")
                        .value("EVENT_STAFF"));
    }

    @Test
    void create_shouldRejectMissingName() throws Exception {

        String json = "{"
                + "\"code\":\"EVENT_STAFF\","
                + "\"name\":\"\""
                + "}";

        mockMvc.perform(
                post("/api/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(roleService);
    }

    @Test
    void update_shouldReturn200() throws Exception {

        when(roleService.update(
                eq(2L),
                any(RoleRequest.class))).thenReturn(
                        response(
                                2L,
                                "EVENT_STAFF",
                                "Panitia Kompetisi",
                                true));

        String json = "{"
                + "\"code\":\"EVENT_STAFF\","
                + "\"name\":\"Panitia Kompetisi\""
                + "}";

        mockMvc.perform(
                put("/api/roles/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name")
                        .value("Panitia Kompetisi"));
    }

    @Test
    void updateStatus_shouldReturn200() throws Exception {

        when(roleService.setActive(2L, false))
                .thenReturn(
                        response(
                                2L,
                                "EVENT_STAFF",
                                "Panitia Event",
                                false));

        mockMvc.perform(
                patch("/api/roles/2/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"active\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active")
                        .value(false));

        verify(roleService).setActive(2L, false);
    }

    @Test
    void updateStatus_shouldRejectMissingStatus()
            throws Exception {

        mockMvc.perform(
                patch("/api/roles/2/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(roleService);
    }

    // =========================================
    // RESPONSE TEST DATA
    // =========================================

    private RoleResponse response(
            Long id,
            String code,
            String name,
            boolean active) {

        return new RoleResponse(
                id,
                code,
                name,
                null,
                active);
    }
}
