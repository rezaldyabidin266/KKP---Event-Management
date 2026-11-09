
package com.skyline.skatecross.modules.user.controller;

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

import com.skyline.skatecross.modules.user.dto.ChangePasswordRequestDTO;
import com.skyline.skatecross.modules.user.dto.UserCreateRequestDTO;
import com.skyline.skatecross.modules.user.dto.UserResponseDTO;
import com.skyline.skatecross.modules.user.dto.UserUpdateRequestDTO;
import com.skyline.skatecross.modules.user.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "API untuk mengelola data User, "
        + "registrasi akun, pembaruan profil, "
        + "penghapusan User, dan perubahan password.")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ============================================
    // GET ALL USERS
    // ============================================

    @Operation(summary = "Get All Users", description = "Mengambil seluruh data User "
            + "yang tersedia dalam sistem.")
    @ApiResponse(responseCode = "200", description = "Data User berhasil diambil. "
            + "Mengembalikan array kosong jika tidak ada data.")
    @GetMapping
    public List<UserResponseDTO> findAll() {
        return userService.findAll();
    }

    // ============================================
    // GET USER BY ID
    // ============================================

    @Operation(summary = "Get User By ID", description = "Mengambil detail User berdasarkan ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Detail User berhasil ditemukan."),
            @ApiResponse(responseCode = "400", description = "Format ID User tidak valid.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "User dengan ID tersebut tidak ditemukan.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public UserResponseDTO findById(
            @Parameter(description = "ID User yang ingin diambil.", example = "1") @PathVariable("id") Long id) {
        return userService.findById(id);
    }

    // ============================================
    // CREATE USER
    // ============================================

    @Operation(summary = "Create User", description = "Mendaftarkan User baru ke sistem. "
            + "Email harus unik dan Role harus valid.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User berhasil didaftarkan."),
            @ApiResponse(responseCode = "400", description = "Data request tidak valid. "
                    + "Contoh: nama kosong, format email salah, "
                    + "password tidak memenuhi validasi, "
                    + "atau field wajib tidak diisi.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Role yang dipilih tidak ditemukan, "
                    + "jika Service menggunakan status 404 "
                    + "untuk Role yang tidak tersedia.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Email sudah digunakan "
                    + "oleh User lain.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDTO create(
            @Valid @RequestBody UserCreateRequestDTO request) {
        return userService.create(request);
    }

    // ============================================
    // UPDATE USER
    // ============================================

    @Operation(summary = "Update User", description = "Memperbarui data profil User "
            + "berdasarkan ID. Email harus tetap unik.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Data User berhasil diperbarui."),
            @ApiResponse(responseCode = "400", description = "Data request tidak valid. "
                    + "Contoh: nama kosong, format email salah, "
                    + "atau data tidak memenuhi validasi.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "User atau referensi data "
                    + "yang diperlukan tidak ditemukan.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Email baru sudah digunakan "
                    + "oleh User lain.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public UserResponseDTO update(
            @Parameter(description = "ID User yang ingin diperbarui.", example = "2") @PathVariable("id") Long id,

            @Valid @RequestBody UserUpdateRequestDTO request) {
        return userService.update(id, request);
    }

    // ============================================
    // DELETE USER
    // ============================================

    @Operation(summary = "Delete User", description = "Menghapus User berdasarkan ID. "
            + "Penghapusan mengikuti implementasi "
            + "UserService. Jika menggunakan hard delete, "
            + "relasi database harus diperhatikan.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User berhasil dihapus. "
                    + "Tidak mengembalikan response body."),
            @ApiResponse(responseCode = "400", description = "Permintaan penghapusan ditolak "
                    + "oleh validasi business logic.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "User dengan ID tersebut tidak ditemukan.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "User tidak dapat dihapus karena "
                    + "masih memiliki relasi data pada database.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @Parameter(description = "ID User yang ingin dihapus.", example = "2") @PathVariable("id") Long id) {
        userService.delete(id);
    }

    // ============================================
    // CHANGE PASSWORD
    // ============================================

    @Operation(summary = "Change Password", description = "Mengubah password User berdasarkan ID. "
            + "Password lama harus valid dan "
            + "konfirmasi password baru harus cocok.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password berhasil diubah. "
                    + "Tidak mengembalikan response body."),
            @ApiResponse(responseCode = "400", description = "Password lama salah, "
                    + "password baru tidak valid, "
                    + "atau konfirmasi password tidak cocok.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "User dengan ID tersebut tidak ditemukan.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PatchMapping("/{id}/password")
    public void changePassword(
            @Parameter(description = "ID User yang password-nya ingin diubah.", example = "2") @PathVariable("id") Long id,

            @Valid @RequestBody ChangePasswordRequestDTO request) {
        userService.changePassword(id, request);
    }
}
