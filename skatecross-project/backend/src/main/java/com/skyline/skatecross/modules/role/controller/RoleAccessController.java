
package com.skyline.skatecross.modules.role.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.skyline.skatecross.common.exception.ApiErrorResponse;

import com.skyline.skatecross.modules.role.dto.RoleAccessRequest;
import com.skyline.skatecross.modules.role.dto.RoleAccessResponse;
import com.skyline.skatecross.modules.role.service.RoleAccessService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/roles/{roleId}/access")
@Tag(name = "Role Access Management", description = "API untuk mengelola permission Role "
        + "berdasarkan Page Key, termasuk View, Create, "
        + "Edit, Delete, dan Approve.")
public class RoleAccessController {

    private final RoleAccessService roleAccessService;

    public RoleAccessController(
            RoleAccessService roleAccessService) {

        this.roleAccessService = roleAccessService;
    }

    // ============================================
    // GET ALL PERMISSIONS
    // ============================================

    @Operation(summary = "Get All Role Permissions", description = "Mengambil seluruh konfigurasi permission "
            + "milik Role berdasarkan ID. "
            + "Data diurutkan berdasarkan Page Key.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Seluruh permission Role berhasil diambil. "
                    + "Mengembalikan array kosong jika Role "
                    + "belum memiliki permission.", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = RoleAccessResponse.class)))),
            @ApiResponse(responseCode = "400", description = "ID Role tidak valid.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Role tidak ditemukan.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping
    public List<RoleAccessResponse> findAll(
            @Parameter(description = "ID Role", example = "2") @PathVariable("roleId") Long roleId) {
        return roleAccessService.findAllByRoleId(roleId);
    }

    // ============================================
    // GET PERMISSION BY PAGE KEY
    // ============================================

    @Operation(summary = "Get Role Permission By Page Key", description = "Mengambil konfigurasi permission "
            + "Role untuk satu Page Key tertentu.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Permission ditemukan.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoleAccessResponse.class))),
            @ApiResponse(responseCode = "400", description = "ID Role atau format Page Key tidak valid.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Role atau permission tidak ditemukan.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/{pageKey}")
    public RoleAccessResponse findByPageKey(
            @Parameter(description = "ID Role", example = "2") @PathVariable("roleId") Long roleId,

            @Parameter(description = "Kode halaman atau fitur", example = "USER_MANAGEMENT") @PathVariable("pageKey") String pageKey) {
        return roleAccessService.findByPageKey(
                roleId,
                pageKey);
    }

    // ============================================
    // UPSERT PERMISSION
    // ============================================

    @Operation(summary = "Create Or Update Role Permission", description = "Membuat permission baru apabila belum ada, "
            + "atau memperbarui permission yang sudah tersedia. "
            + "Seluruh nilai permission wajib dikirim "
            + "sebagai boolean true atau false. "
            + "Page Key otomatis dinormalisasi ke huruf kapital.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Permission berhasil dibuat atau diperbarui.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoleAccessResponse.class))),
            @ApiResponse(responseCode = "400", description = "Request tidak valid. "
                    + "Contoh: permission tidak lengkap, "
                    + "Page Key salah, atau ID Role tidak valid.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Role tidak ditemukan.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Terjadi konflik constraint database, "
                    + "misalnya permintaan pembuatan permission "
                    + "yang sama dilakukan bersamaan.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PutMapping("/{pageKey}")
    public RoleAccessResponse upsert(
            @Parameter(description = "ID Role", example = "2") @PathVariable("roleId") Long roleId,

            @Parameter(description = "Kode halaman atau fitur", example = "USER_MANAGEMENT") @PathVariable("pageKey") String pageKey,

            @Valid @RequestBody RoleAccessRequest request) {
        return roleAccessService.upsert(
                roleId,
                pageKey,
                request);
    }

    // ============================================
    // DELETE PERMISSION
    // ============================================

    @Operation(summary = "Delete Role Permission", description = "Menghapus konfigurasi permission "
            + "berdasarkan Role ID dan Page Key. "
            + "Operasi ini tidak menghapus Role atau User.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Permission berhasil dihapus. "
                    + "Response tidak memiliki body."),
            @ApiResponse(responseCode = "400", description = "ID Role atau Page Key tidak valid.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Role atau permission tidak ditemukan.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @DeleteMapping("/{pageKey}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @Parameter(description = "ID Role", example = "2") @PathVariable("roleId") Long roleId,

            @Parameter(description = "Kode halaman atau fitur", example = "USER_MANAGEMENT") @PathVariable("pageKey") String pageKey) {
        roleAccessService.delete(
                roleId,
                pageKey);
    }
}
