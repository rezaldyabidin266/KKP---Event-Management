
package com.skyline.skatecross.modules.role.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import com.skyline.skatecross.modules.role.dto.RoleRequest;
import com.skyline.skatecross.modules.role.dto.RoleResponse;
import com.skyline.skatecross.modules.role.entity.Role;
import com.skyline.skatecross.modules.role.mapper.RoleMapper;
import com.skyline.skatecross.modules.role.repository.RoleRepository;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    private RoleService roleService;

    @BeforeEach
    void setUp() {
        roleService = new RoleServiceImpl(
                roleRepository,
                new RoleMapper());
    }

    @Test
    void findAll_shouldReturnRoles() {

        when(roleRepository.findAll(
                Sort.by(Sort.Direction.ASC, "id"))).thenReturn(Arrays.asList(
                        createRole(1L, "SUPER_ADMIN", "Super Admin"),
                        createRole(2L, "EVENT_STAFF", "Panitia Event")));

        List<RoleResponse> result = roleService.findAll();

        assertEquals(2, result.size());
        assertEquals("SUPER_ADMIN", result.get(0).getCode());
        assertEquals("EVENT_STAFF", result.get(1).getCode());
    }

    @Test
    void findById_shouldReturnRole() {

        Role role = createRole(
                1L,
                "SUPER_ADMIN",
                "Super Admin");

        when(roleRepository.findById(1L))
                .thenReturn(Optional.of(role));

        RoleResponse result = roleService.findById(1L);

        assertEquals(1L, result.getId());
        assertEquals("SUPER_ADMIN", result.getCode());
        assertTrue(result.isActive());
    }

    @Test
    void findById_shouldReturn404WhenNotFound() {

        when(roleRepository.findById(99L))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> roleService.findById(99L));

        assertEquals(
                HttpStatus.NOT_FOUND,
                exception.getStatus());
    }

    @Test
    void create_shouldNormalizeCodeAndSave() {

        RoleRequest request = createRequest(
                " event_staff ",
                " Panitia Event ");

        when(roleRepository.save(any(Role.class)))
                .thenAnswer(invocation -> {

                    Role role = invocation.getArgument(0);

                    ReflectionTestUtils.setField(
                            role,
                            "id",
                            2L);

                    return role;
                });

        RoleResponse result = roleService.create(request);

        assertEquals(2L, result.getId());
        assertEquals("EVENT_STAFF", result.getCode());
        assertEquals("Panitia Event", result.getName());
        assertTrue(result.isActive());

        verify(roleRepository)
                .existsByCode("EVENT_STAFF");

        verify(roleRepository).save(any(Role.class));
    }

    @Test
    void create_shouldRejectDuplicateCode() {

        RoleRequest request = createRequest(
                "EVENT_STAFF",
                "Panitia Event");

        when(roleRepository.existsByCode("EVENT_STAFF"))
                .thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> roleService.create(request));

        assertEquals(
                HttpStatus.CONFLICT,
                exception.getStatus());

        verify(roleRepository, never())
                .save(any(Role.class));
    }

    @Test
    void create_shouldRejectInvalidCode() {

        RoleRequest request = createRequest(
                "EVENT-STAFF",
                "Panitia Event");

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> roleService.create(request));

        assertEquals(
                HttpStatus.BAD_REQUEST,
                exception.getStatus());

        verifyNoInteractions(roleRepository);
    }

    @Test
    void update_shouldModifyExistingRole() {

        Role existing = createRole(
                2L,
                "EVENT_STAFF",
                "Panitia Event");

        when(roleRepository.findById(2L))
                .thenReturn(Optional.of(existing));

        when(roleRepository.findByCode("EVENT_STAFF"))
                .thenReturn(Optional.of(existing));

        when(roleRepository.save(any(Role.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RoleRequest request = createRequest(
                "EVENT_STAFF",
                "Panitia Kompetisi");

        RoleResponse result = roleService.update(
                2L,
                request);

        assertEquals(
                "Panitia Kompetisi",
                result.getName());

        assertEquals("EVENT_STAFF", result.getCode());

        verify(roleRepository).save(existing);
    }

    @Test
    void update_shouldRejectDuplicateCode() {

        Role current = createRole(
                2L,
                "EVENT_STAFF",
                "Panitia Event");

        Role another = createRole(
                3L,
                "JUDGE",
                "Juri");

        when(roleRepository.findById(2L))
                .thenReturn(Optional.of(current));

        when(roleRepository.findByCode("JUDGE"))
                .thenReturn(Optional.of(another));

        RoleRequest request = createRequest(
                "JUDGE",
                "Juri");

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> roleService.update(2L, request));

        assertEquals(
                HttpStatus.CONFLICT,
                exception.getStatus());

        verify(roleRepository, never())
                .save(any(Role.class));
    }

    @Test
    void update_shouldProtectSuperAdminCode() {

        Role role = createRole(
                1L,
                "SUPER_ADMIN",
                "Super Admin");

        when(roleRepository.findById(1L))
                .thenReturn(Optional.of(role));

        RoleRequest request = createRequest(
                "ADMIN",
                "Administrator");

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> roleService.update(1L, request));

        assertEquals(
                HttpStatus.BAD_REQUEST,
                exception.getStatus());
    }

    @Test
    void setActive_shouldDeactivateRegularRole() {

        Role role = createRole(
                2L,
                "EVENT_STAFF",
                "Panitia Event");

        when(roleRepository.findById(2L))
                .thenReturn(Optional.of(role));

        when(roleRepository.save(any(Role.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RoleResponse result = roleService.setActive(
                2L,
                false);

        assertFalse(result.isActive());

        verify(roleRepository).save(role);
    }

    @Test
    void setActive_shouldProtectSuperAdmin() {

        Role role = createRole(
                1L,
                "SUPER_ADMIN",
                "Super Admin");

        when(roleRepository.findById(1L))
                .thenReturn(Optional.of(role));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> roleService.setActive(1L, false));

        assertEquals(
                HttpStatus.BAD_REQUEST,
                exception.getStatus());

        verify(roleRepository, never())
                .save(any(Role.class));
    }

    // =========================================
    // TEST DATA HELPERS
    // =========================================

    private Role createRole(
            Long id,
            String code,
            String name) {

        Role role = new Role();

        ReflectionTestUtils.setField(
                role,
                "id",
                id);

        role.setCode(code);
        role.setName(name);
        role.setActive(true);

        return role;
    }

    private RoleRequest createRequest(
            String code,
            String name) {

        RoleRequest request = new RoleRequest();

        request.setCode(code);
        request.setName(name);

        return request;
    }
}
