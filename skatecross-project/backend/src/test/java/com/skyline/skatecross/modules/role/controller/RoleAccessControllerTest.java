
package com.skyline.skatecross.modules.role.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.server.ResponseStatusException;

import com.skyline.skatecross.common.exception.GlobalExceptionHandler;
import com.skyline.skatecross.modules.role.dto.RoleAccessRequest;
import com.skyline.skatecross.modules.role.dto.RoleAccessResponse;
import com.skyline.skatecross.modules.role.service.RoleAccessService;

@ExtendWith(MockitoExtension.class)
class RoleAccessControllerTest {

    @Mock
    private RoleAccessService roleAccessService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();

        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(
                        new RoleAccessController(roleAccessService))
                .setValidator(validator)
                .setControllerAdvice(
                        new GlobalExceptionHandler())
                .build();
    }

    @Test
    void findAll_shouldReturn200() throws Exception {

        when(roleAccessService.findAllByRoleId(2L))
                .thenReturn(Arrays.asList(
                        response(
                                10L, "USER_MANAGEMENT"),
                        response(
                                11L, "EVENT_MANAGEMENT")));

        mockMvc.perform(
                get("/api/roles/2/access"))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[0].pageKey")
                                .value("USER_MANAGEMENT"))
                .andExpect(
                        jsonPath("$[1].pageKey")
                                .value("EVENT_MANAGEMENT"));
    }

    @Test
    void findAll_shouldReturn404WhenRoleMissing()
            throws Exception {

        when(roleAccessService.findAllByRoleId(99L))
                .thenThrow(
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Role tidak ditemukan"));

        mockMvc.perform(
                get("/api/roles/99/access"))
                .andExpect(status().isNotFound())
                .andExpect(
                        jsonPath("$.status").value(404))
                .andExpect(
                        jsonPath("$.message")
                                .value("Role tidak ditemukan"));
    }

    @Test
    void findByPageKey_shouldReturn200()
            throws Exception {

        when(roleAccessService.findByPageKey(
                2L, "USER_MANAGEMENT")).thenReturn(
                        response(
                                10L, "USER_MANAGEMENT"));

        mockMvc.perform(
                get(
                        "/api/roles/2/access/USER_MANAGEMENT"))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.pageKey")
                                .value("USER_MANAGEMENT"))
                .andExpect(
                        jsonPath("$.roleId").value(2));
    }

    @Test
    void findByPageKey_shouldReturn404()
            throws Exception {

        when(roleAccessService.findByPageKey(
                2L, "UNKNOWN_PAGE")).thenThrow(
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Permission tidak ditemukan"));

        mockMvc.perform(
                get(
                        "/api/roles/2/access/UNKNOWN_PAGE"))
                .andExpect(status().isNotFound())
                .andExpect(
                        jsonPath("$.message")
                                .value("Permission tidak ditemukan"));
    }

    @Test
    void upsert_shouldReturn200()
            throws Exception {

        when(roleAccessService.upsert(
                eq(2L),
                eq("USER_MANAGEMENT"),
                any(RoleAccessRequest.class))).thenReturn(
                        response(
                                10L, "USER_MANAGEMENT"));

        mockMvc.perform(
                put(
                        "/api/roles/2/access/USER_MANAGEMENT")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson()))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id").value(10))
                .andExpect(
                        jsonPath("$.pageKey")
                                .value("USER_MANAGEMENT"));

        verify(roleAccessService).upsert(
                eq(2L),
                eq("USER_MANAGEMENT"),
                any(RoleAccessRequest.class));
    }

    @Test
    void upsert_shouldRejectMissingPermission()
            throws Exception {

        String json = "{"
                + "\"canView\":true,"
                + "\"canCreate\":true,"
                + "\"canEdit\":false,"
                + "\"canDelete\":false"
                + "}";

        mockMvc.perform(
                put(
                        "/api/roles/2/access/USER_MANAGEMENT")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.fieldErrors.canApprove")
                                .exists());

        verifyNoInteractions(roleAccessService);
    }

    @Test
    void upsert_shouldRejectInvalidBoolean()
            throws Exception {

        String json = "{"
                + "\"canView\":\"invalid\","
                + "\"canCreate\":true,"
                + "\"canEdit\":false,"
                + "\"canDelete\":false,"
                + "\"canApprove\":false"
                + "}";

        mockMvc.perform(
                put(
                        "/api/roles/2/access/USER_MANAGEMENT")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(roleAccessService);
    }

    @Test
    void delete_shouldReturn204()
            throws Exception {

        mockMvc.perform(
                delete(
                        "/api/roles/2/access/USER_MANAGEMENT"))
                .andExpect(status().isNoContent());

        verify(roleAccessService).delete(
                2L, "USER_MANAGEMENT");
    }

    @Test
    void delete_shouldReturn404()
            throws Exception {

        doThrow(
                new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Permission tidak ditemukan"))
                .when(roleAccessService).delete(
                        2L, "UNKNOWN_PAGE");

        mockMvc.perform(
                delete(
                        "/api/roles/2/access/UNKNOWN_PAGE"))
                .andExpect(status().isNotFound())
                .andExpect(
                        jsonPath("$.status").value(404))
                .andExpect(
                        jsonPath("$.message")
                                .value("Permission tidak ditemukan"));
    }

    // ============================================
    // HELPERS
    // ============================================

    private RoleAccessResponse response(
            Long id,
            String pageKey) {

        return new RoleAccessResponse(
                id,
                2L,
                pageKey,
                true,
                true,
                false,
                false,
                false);
    }

    private String validJson() {

        return "{"
                + "\"canView\":true,"
                + "\"canCreate\":true,"
                + "\"canEdit\":false,"
                + "\"canDelete\":false,"
                + "\"canApprove\":false"
                + "}";
    }
}
