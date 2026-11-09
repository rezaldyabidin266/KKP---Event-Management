
package com.skyline.skatecross.modules.role.service;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.skyline.skatecross.modules.role.dto.RoleAccessRequest;
import com.skyline.skatecross.modules.role.dto.RoleAccessResponse;

import com.skyline.skatecross.modules.role.entity.Role;
import com.skyline.skatecross.modules.role.entity.RoleAccess;

import com.skyline.skatecross.modules.role.mapper.RoleAccessMapper;

import com.skyline.skatecross.modules.role.repository.RoleAccessRepository;
import com.skyline.skatecross.modules.role.repository.RoleRepository;

@Service
@Transactional(readOnly = true)
public class RoleAccessServiceImpl implements RoleAccessService {

    private final RoleRepository roleRepository;
    private final RoleAccessRepository roleAccessRepository;
    private final RoleAccessMapper roleAccessMapper;

    public RoleAccessServiceImpl(
            RoleRepository roleRepository,
            RoleAccessRepository roleAccessRepository,
            RoleAccessMapper roleAccessMapper) {

        this.roleRepository = roleRepository;
        this.roleAccessRepository = roleAccessRepository;
        this.roleAccessMapper = roleAccessMapper;
    }

    // ============================================
    // GET ALL PERMISSIONS BY ROLE
    // ============================================

    @Override
    public List<RoleAccessResponse> findAllByRoleId(
            Long roleId) {

        findRoleOrThrow(roleId);

        return roleAccessRepository
                .findAllByRole_Id(roleId)
                .stream()
                .sorted(
                        Comparator.comparing(
                                RoleAccess::getPageKey))
                .map(roleAccessMapper::toResponse)
                .collect(Collectors.toList());
    }

    // ============================================
    // GET PERMISSION BY PAGE KEY
    // ============================================

    @Override
    public RoleAccessResponse findByPageKey(
            Long roleId,
            String pageKey) {

        String normalizedPageKey = normalizePageKey(pageKey);

        findRoleOrThrow(roleId);

        RoleAccess roleAccess = roleAccessRepository
                .findByRole_IdAndPageKey(
                        roleId,
                        normalizedPageKey)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Permission '" + normalizedPageKey
                                + "' untuk Role ID " + roleId
                                + " tidak ditemukan"));

        return roleAccessMapper.toResponse(roleAccess);
    }

    // ============================================
    // CREATE OR UPDATE PERMISSION
    // ============================================

    @Override
    @Transactional
    public RoleAccessResponse upsert(
            Long roleId,
            String pageKey,
            RoleAccessRequest request) {

        String normalizedPageKey = normalizePageKey(pageKey);

        validateRequest(request);

        Role role = findRoleOrThrow(roleId);

        RoleAccess roleAccess = roleAccessRepository
                .findByRole_IdAndPageKey(
                        roleId,
                        normalizedPageKey)
                .orElseGet(() -> {

                    RoleAccess newRoleAccess = new RoleAccess();

                    newRoleAccess.setRole(role);
                    newRoleAccess.setPageKey(
                            normalizedPageKey);

                    return newRoleAccess;
                });

        roleAccess.setCanView(
                request.getCanView());

        roleAccess.setCanCreate(
                request.getCanCreate());

        roleAccess.setCanEdit(
                request.getCanEdit());

        roleAccess.setCanDelete(
                request.getCanDelete());

        roleAccess.setCanApprove(
                request.getCanApprove());

        RoleAccess saved = roleAccessRepository.save(roleAccess);

        return roleAccessMapper.toResponse(saved);
    }

    // ============================================
    // DELETE PERMISSION
    // ============================================

    @Override
    @Transactional
    public void delete(
            Long roleId,
            String pageKey) {

        String normalizedPageKey = normalizePageKey(pageKey);

        findRoleOrThrow(roleId);

        RoleAccess roleAccess = roleAccessRepository
                .findByRole_IdAndPageKey(
                        roleId,
                        normalizedPageKey)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Permission '" + normalizedPageKey
                                + "' untuk Role ID " + roleId
                                + " tidak ditemukan"));

        roleAccessRepository.delete(roleAccess);
    }

    // ============================================
    // PRIVATE: FIND ROLE
    // ============================================

    private Role findRoleOrThrow(Long roleId) {

        if (roleId == null || roleId <= 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "ID Role harus lebih dari 0");
        }

        return roleRepository.findById(roleId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Role dengan ID " + roleId
                                + " tidak ditemukan"));
    }

    // ============================================
    // PRIVATE: NORMALIZE PAGE KEY
    // ============================================

    private String normalizePageKey(String pageKey) {

        if (pageKey == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Page Key wajib diisi");
        }

        String normalized = pageKey
                .trim()
                .toUpperCase(Locale.ROOT);

        if (normalized.length() > 100
                || !normalized.matches("[A-Z][A-Z0-9_]*")) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Page Key harus diawali huruf, "
                            + "hanya boleh berisi huruf A-Z, angka 0-9, "
                            + "atau underscore, dan maksimal 100 karakter");
        }

        return normalized;
    }

    // ============================================
    // PRIVATE: VALIDATE REQUEST
    // ============================================

    private void validateRequest(RoleAccessRequest request) {

        if (request == null
                || request.getCanView() == null
                || request.getCanCreate() == null
                || request.getCanEdit() == null
                || request.getCanDelete() == null
                || request.getCanApprove() == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Seluruh permission wajib diisi "
                            + "dengan nilai true atau false");
        }
    }
}
