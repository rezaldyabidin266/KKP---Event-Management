
package com.skyline.skatecross.modules.role.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.skyline.skatecross.common.exception.ApiErrorResponse;

import com.skyline.skatecross.modules.role.dto.RoleRequest;
import com.skyline.skatecross.modules.role.dto.RoleResponse;
import com.skyline.skatecross.modules.role.dto.RoleStatusRequest;
import com.skyline.skatecross.modules.role.service.RoleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/roles")
@Tag(name = "Role Management", description = "API untuk mengelola master Role, "
        + "termasuk pembuatan, perubahan, "
        + "aktivasi, deaktivasi, dan Soft Delete.")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    // ============================================
    // GET ALL ROLES
    // ============================================

    @Operation(summary = "Get All Roles", description = "Mengambil seluruh data Role, "
            + "termasuk Role aktif dan nonaktif, "
            + "diurutkan berdasarkan ID.")
    @ApiResponse(responseCode = "200", description = "Data Role berhasil diambil.")
    @GetMapping
    public List<RoleResponse> findAll() {
        return roleService.findAll();
    }

    // ============================================
    // GET ROLE BY ID
    // ============================================

    @Operation(summary = "Get Role By ID", description = "Mengambil detail Role berdasarkan ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Role berhasil ditemukan."),
            @ApiResponse(responseCode = "400", description = "Format ID Role tidak valid.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Role dengan ID tersebut tidak ditemukan.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public RoleResponse findById(
            @Parameter(description = "ID Role yang ingin diambil.", example = "1") @PathVariable("id") Long id) {
        return roleService.findById(id);
    }

    // ============================================
    // CREATE ROLE
    // ============================================

    @Operation(summary = "Create Role", description = "Membuat Role baru. "
            + "Kode Role dinormalisasi menjadi huruf kapital "
            + "dan tidak boleh duplikat.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Role baru berhasil dibuat."),
            @ApiResponse(responseCode = "400", description = "Data request tidak valid. "
                    + "Contoh: kode kosong, nama kosong, "
                    + "atau format kode Role tidak sesuai.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Kode Role sudah digunakan "
                    + "oleh Role lain.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
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

    @Operation(summary = "Update Role", description = "Memperbarui kode, nama, dan deskripsi Role. "
            + "Kode harus tetap unik dan kode SUPER_ADMIN "
            + "tidak boleh diubah.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Role berhasil diperbarui."),
            @ApiResponse(responseCode = "400", description = "Data request tidak valid "
                    + "atau perubahan kode SUPER_ADMIN ditolak.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Role dengan ID tersebut tidak ditemukan.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Kode Role sudah digunakan "
                    + "oleh Role lain.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public RoleResponse update(
            @Parameter(description = "ID Role yang ingin diperbarui.", example = "2") @PathVariable("id") Long id,

            @Valid @RequestBody RoleRequest request) {
        return roleService.update(id, request);
    }

    // ============================================
    // UPDATE ROLE STATUS
    // ============================================

    @Operation(summary = "Update Role Status", description = "Mengaktifkan atau menonaktifkan Role. "
            + "Gunakan active=true untuk aktivasi "
            + "dan active=false untuk deaktivasi. "
            + "Role SUPER_ADMIN tidak boleh dinonaktifkan.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status Role berhasil diperbarui."),
            @ApiResponse(responseCode = "400", description = "Nilai active tidak valid "
                    + "atau mencoba menonaktifkan SUPER_ADMIN.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Role dengan ID tersebut tidak ditemukan.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PatchMapping("/{id}/status")
    public RoleResponse updateStatus(
            @Parameter(description = "ID Role yang statusnya ingin diubah.", example = "2") @PathVariable("id") Long id,

            @Valid @RequestBody RoleStatusRequest request) {
        return roleService.setActive(
                id,
                request.getActive());
    }

    // ============================================
    // DELETE ROLE (SOFT DELETE)
    // ============================================

    @Operation(summary = "Delete Role", description = "Melakukan Soft Delete dengan "
            + "mengubah status Role menjadi tidak aktif. "
            + "Data tidak dihapus secara fisik. "
            + "Role SUPER_ADMIN tidak boleh dihapus.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Role berhasil dinonaktifkan. "
                    + "Tidak mengembalikan response body."),
            @ApiResponse(responseCode = "400", description = "Penghapusan tidak diizinkan, "
                    + "misalnya mencoba menghapus SUPER_ADMIN.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Role dengan ID tersebut tidak ditemukan.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @Parameter(description = "ID Role yang ingin dinonaktifkan.", example = "2") @PathVariable("id") Long id) {
        roleService.setActive(id, false);
    }
}
