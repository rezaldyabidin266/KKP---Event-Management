
package com.skyline.skatecross.modules.role.service;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.skyline.skatecross.modules.role.dto.RoleRequest;
import com.skyline.skatecross.modules.role.dto.RoleResponse;
import com.skyline.skatecross.modules.role.entity.Role;
import com.skyline.skatecross.modules.role.mapper.RoleMapper;
import com.skyline.skatecross.modules.role.repository.RoleRepository;

@Service
@Transactional(readOnly = true)
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    public RoleServiceImpl(
            RoleRepository roleRepository,
            RoleMapper roleMapper) {

        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
    }

    @Override
    public List<RoleResponse> findAll() {

        return roleRepository
                .findAll(Sort.by(Sort.Direction.ASC, "id"))
                .stream()
                .map(roleMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public RoleResponse findById(Long id) {

        Role role = getRoleOrThrow(id);

        return roleMapper.toResponse(role);
    }

    @Override
    @Transactional
    public RoleResponse create(RoleRequest request) {

        String code = normalizeCode(request.getCode());

        validateCode(code);

        if (roleRepository.existsByCode(code)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Kode role sudah digunakan");
        }

        Role role = new Role();

        role.setCode(code);
        role.setName(request.getName().trim());
        role.setDescription(request.getDescription());
        role.setActive(true);

        Role saved = roleRepository.save(role);

        return roleMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public RoleResponse update(
            Long id,
            RoleRequest request) {

        Role role = getRoleOrThrow(id);

        String code = normalizeCode(request.getCode());

        validateCode(code);

        // Kode SUPER_ADMIN tidak boleh diganti.
        if ("SUPER_ADMIN".equals(role.getCode())
                && !"SUPER_ADMIN".equals(code)) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Kode SUPER_ADMIN tidak boleh diubah");
        }

        boolean duplicate = roleRepository
                .findByCode(code)
                .filter(existing -> !existing.getId().equals(role.getId()))
                .isPresent();

        if (duplicate) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Kode role sudah digunakan");
        }

        role.setCode(code);
        role.setName(request.getName().trim());
        role.setDescription(request.getDescription());

        return roleMapper.toResponse(
                roleRepository.save(role));
    }

    @Override
    @Transactional
    public RoleResponse setActive(Long id, boolean active) {

        Role role = getRoleOrThrow(id);

        if ("SUPER_ADMIN".equals(role.getCode()) && !active) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "SUPER_ADMIN tidak boleh dinonaktifkan");
        }

        role.setActive(active);

        return roleMapper.toResponse(
                roleRepository.save(role));
    }

    private Role getRoleOrThrow(Long id) {

        return roleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Role tidak ditemukan"));
    }

    private String normalizeCode(String code) {

        return code.trim().toUpperCase(Locale.ROOT);
    }

    private void validateCode(String code) {

        if (!code.matches("^[A-Z][A-Z0-9_]*$")) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Kode role hanya boleh huruf kapital, angka, dan underscore");
        }
    }
}
