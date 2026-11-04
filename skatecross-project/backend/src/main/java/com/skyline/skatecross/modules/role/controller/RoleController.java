
package com.skyline.skatecross.modules.role.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.skyline.skatecross.modules.role.dto.RoleRequest;
import com.skyline.skatecross.modules.role.dto.RoleResponse;
import com.skyline.skatecross.modules.role.dto.RoleStatusRequest;
import com.skyline.skatecross.modules.role.service.RoleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/roles")
@Tag(name = "Role Management", description = "API untuk mengelola master Role")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    // ============================================
    // GET ALL ROLES
    // ============================================

    @Operation(summary = "Get All Roles", description = "Mengambil seluruh data Role")
    @ApiResponse(responseCode = "200", description = "Berhasil mengambil data Role")
    @GetMapping
    public List<RoleResponse> findAll() {

        return roleService.findAll();
    }

    // ============================================
    // GET ROLE BY ID
    // ============================================

    @Operation(summary = "Get Role By ID", description = "Mengambil detail Role berdasarkan ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Role ditemukan"),
            @ApiResponse(responseCode = "404", description = "Role tidak ditemukan")
    })
    @GetMapping("/{id}")
    public RoleResponse findById(
            @PathVariable("id") Long id) {

        return roleService.findById(id);
    }

    // ============================================
    // CREATE ROLE
    // ============================================

    @Operation(summary = "Create Role", description = "Membuat master Role baru")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Role berhasil dibuat"),
            @ApiResponse(responseCode = "400", description = "Data Role tidak valid"),
            @ApiResponse(responseCode = "409", description = "Kode Role sudah digunakan")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RoleResponse create(
            @Valid @RequestBody RoleRequest request) {

        return roleService.create(request);
    }

    // ============================================
    // UPDATE ROLE
    // ============================================

    @Operation(summary = "Update Role", description = "Memperbarui master Role berdasarkan ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Role berhasil diperbarui"),
            @ApiResponse(responseCode = "400", description = "Data Role tidak valid"),
            @ApiResponse(responseCode = "404", description = "Role tidak ditemukan"),
            @ApiResponse(responseCode = "409", description = "Kode Role sudah digunakan")
    })
    @PutMapping("/{id}")
    public RoleResponse update(
            @PathVariable("id") Long id,
            @Valid @RequestBody RoleRequest request) {

        return roleService.update(id, request);
    }

    // ============================================
    // UPDATE ROLE STATUS
    // ============================================

    @Operation(summary = "Update Role Status", description = "Mengaktifkan atau menonaktifkan Role")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status Role berhasil diperbarui"),
            @ApiResponse(responseCode = "400", description = "Perubahan status tidak diizinkan"),
            @ApiResponse(responseCode = "404", description = "Role tidak ditemukan")
    })
    @PatchMapping("/{id}/status")
    public RoleResponse updateStatus(
            @PathVariable("id") Long id,
            @Valid @RequestBody RoleStatusRequest request) {

        return roleService.setActive(
                id,
                request.getActive());
    }
}
