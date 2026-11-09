
package com.skyline.skatecross.modules.role.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import com.skyline.skatecross.modules.role.dto.RoleAccessRequest;
import com.skyline.skatecross.modules.role.dto.RoleAccessResponse;
import com.skyline.skatecross.modules.role.entity.Role;
import com.skyline.skatecross.modules.role.entity.RoleAccess;
import com.skyline.skatecross.modules.role.mapper.RoleAccessMapper;
import com.skyline.skatecross.modules.role.repository.RoleRepository;
import com.skyline.skatecross.modules.role.repository.RoleAccessRepository;

@ExtendWith(MockitoExtension.class)
class RoleAccessServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RoleAccessRepository roleAccessRepository;

    private RoleAccessService service;

    @BeforeEach
    void setUp() {
        service = new RoleAccessServiceImpl(
                roleRepository,
                roleAccessRepository,
                new RoleAccessMapper());
    }

    @Test
    void findAll_shouldReturnSortedPermissions() {

        Role role = createRole(2L);

        when(roleRepository.findById(2L))
                .thenReturn(Optional.of(role));

        when(roleAccessRepository.findAllByRole_Id(2L))
                .thenReturn(Arrays.asList(
                        createAccess(2L, role, "USER_MANAGEMENT"),
                        createAccess(1L, role, "EVENT_MANAGEMENT")));

        java.util.List<RoleAccessResponse> result = service.findAllByRoleId(2L);

        assertEquals(2, result.size());
        assertEquals(
                "EVENT_MANAGEMENT",
                result.get(0).getPageKey());
        assertEquals(
                "USER_MANAGEMENT",
                result.get(1).getPageKey());
    }

    @Test
    void findAll_shouldReturnEmptyList() {

        Role role = createRole(2L);

        when(roleRepository.findById(2L))
                .thenReturn(Optional.of(role));

        when(roleAccessRepository.findAllByRole_Id(2L))
                .thenReturn(Collections.emptyList());

        assertTrue(
                service.findAllByRoleId(2L).isEmpty());
    }

    @Test
    void findAll_shouldReturn404WhenRoleNotFound() {

        when(roleRepository.findById(99L))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.findAllByRoleId(99L));

        assertEquals(
                HttpStatus.NOT_FOUND,
                exception.getStatus());

        verifyNoInteractions(roleAccessRepository);
    }

    @Test
    void findByPageKey_shouldNormalizePageKey() {

        Role role = createRole(2L);

        RoleAccess access = createAccess(
                10L, role, "USER_MANAGEMENT");

        when(roleRepository.findById(2L))
                .thenReturn(Optional.of(role));

        when(roleAccessRepository.findByRole_IdAndPageKey(
                2L, "USER_MANAGEMENT")).thenReturn(Optional.of(access));

        RoleAccessResponse result = service.findByPageKey(
                2L, " user_management ");

        assertEquals(
                "USER_MANAGEMENT",
                result.getPageKey());

        assertEquals(Long.valueOf(2L), result.getRoleId());
    }

    @Test
    void findByPageKey_shouldReturn404WhenMissing() {

        when(roleRepository.findById(2L))
                .thenReturn(Optional.of(createRole(2L)));

        when(roleAccessRepository.findByRole_IdAndPageKey(
                2L, "UNKNOWN_PAGE")).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.findByPageKey(
                        2L, "UNKNOWN_PAGE"));

        assertEquals(
                HttpStatus.NOT_FOUND,
                exception.getStatus());
    }

    @Test
    void upsert_shouldCreatePermission() {

        Role role = createRole(2L);

        when(roleRepository.findById(2L))
                .thenReturn(Optional.of(role));

        when(roleAccessRepository.findByRole_IdAndPageKey(
                2L, "EVENT_MANAGEMENT")).thenReturn(Optional.empty());

        when(roleAccessRepository.save(any(RoleAccess.class)))
                .thenAnswer(invocation -> {

                    RoleAccess access = invocation.getArgument(0);

                    ReflectionTestUtils.setField(
                            access, "id", 15L);

                    return access;
                });

        RoleAccessRequest request = createRequest(true, true, false, false, false);

        RoleAccessResponse result = service.upsert(
                2L, "event_management", request);

        assertEquals(Long.valueOf(15L), result.getId());
        assertEquals(
                "EVENT_MANAGEMENT",
                result.getPageKey());
        assertTrue(result.getCanView());
        assertTrue(result.getCanCreate());
        assertFalse(result.getCanEdit());

        ArgumentCaptor<RoleAccess> captor = ArgumentCaptor.forClass(RoleAccess.class);

        verify(roleAccessRepository).save(captor.capture());

        assertSame(
                role,
                captor.getValue().getRole());
    }

    @Test
    void upsert_shouldUpdateExistingPermission() {

        Role role = createRole(2L);

        RoleAccess access = createAccess(
                10L, role, "USER_MANAGEMENT");

        when(roleRepository.findById(2L))
                .thenReturn(Optional.of(role));

        when(roleAccessRepository.findByRole_IdAndPageKey(
                2L, "USER_MANAGEMENT")).thenReturn(Optional.of(access));

        when(roleAccessRepository.save(access))
                .thenReturn(access);

        RoleAccessResponse result = service.upsert(
                2L,
                "USER_MANAGEMENT",
                createRequest(true, true, true, false, true));

        assertEquals(Long.valueOf(10L), result.getId());
        assertTrue(result.getCanView());
        assertTrue(result.getCanCreate());
        assertTrue(result.getCanEdit());
        assertFalse(result.getCanDelete());
        assertTrue(result.getCanApprove());

        verify(roleAccessRepository).save(access);
    }

    @Test
    void upsert_shouldRejectMissingPermission() {

        RoleAccessRequest request = createRequest(true, null, false, false, false);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.upsert(
                        2L, "USER_MANAGEMENT", request));

        assertEquals(
                HttpStatus.BAD_REQUEST,
                exception.getStatus());

        verifyNoInteractions(roleRepository);
        verifyNoInteractions(roleAccessRepository);
    }

    @Test
    void upsert_shouldRejectInvalidPageKey() {

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.upsert(
                        2L,
                        "INVALID-PAGE",
                        createRequest(true, false, false, false, false)));

        assertEquals(
                HttpStatus.BAD_REQUEST,
                exception.getStatus());

        verifyNoInteractions(roleRepository);
        verifyNoInteractions(roleAccessRepository);
    }

    @Test
    void delete_shouldRemovePermission() {

        Role role = createRole(2L);

        RoleAccess access = createAccess(
                10L, role, "USER_MANAGEMENT");

        when(roleRepository.findById(2L))
                .thenReturn(Optional.of(role));

        when(roleAccessRepository.findByRole_IdAndPageKey(
                2L, "USER_MANAGEMENT")).thenReturn(Optional.of(access));

        service.delete(2L, "USER_MANAGEMENT");

        verify(roleAccessRepository).delete(access);
    }

    @Test
    void delete_shouldReturn404WhenPermissionMissing() {

        when(roleRepository.findById(2L))
                .thenReturn(Optional.of(createRole(2L)));

        when(roleAccessRepository.findByRole_IdAndPageKey(
                2L, "UNKNOWN_PAGE")).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.delete(
                        2L, "UNKNOWN_PAGE"));

        assertEquals(
                HttpStatus.NOT_FOUND,
                exception.getStatus());

        verify(roleAccessRepository, never())
                .delete(any(RoleAccess.class));
    }

    // ============================================
    // HELPERS
    // ============================================

    private Role createRole(Long id) {

        Role role = new Role();

        ReflectionTestUtils.setField(
                role, "id", id);

        role.setCode("EVENT_STAFF");
        role.setName("Panitia Event");
        role.setActive(true);

        return role;
    }

    private RoleAccess createAccess(
            Long id,
            Role role,
            String pageKey) {

        RoleAccess access = new RoleAccess();

        ReflectionTestUtils.setField(
                access, "id", id);

        access.setRole(role);
        access.setPageKey(pageKey);

        access.setCanView(true);
        access.setCanCreate(false);
        access.setCanEdit(false);
        access.setCanDelete(false);
        access.setCanApprove(false);

        return access;
    }

    private RoleAccessRequest createRequest(
            Boolean view,
            Boolean create,
            Boolean edit,
            Boolean delete,
            Boolean approve) {

        RoleAccessRequest request = new RoleAccessRequest();

        request.setCanView(view);
        request.setCanCreate(create);
        request.setCanEdit(edit);
        request.setCanDelete(delete);
        request.setCanApprove(approve);

        return request;
    }
}
