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

import com.skyline.skatecross.modules.user.dto.ChangePasswordRequestDTO;
import com.skyline.skatecross.modules.user.dto.UserCreateRequestDTO;
import com.skyline.skatecross.modules.user.dto.UserResponseDTO;
import com.skyline.skatecross.modules.user.dto.UserUpdateRequestDTO;
import com.skyline.skatecross.modules.user.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "API untuk mengelola data User")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ============================================
    // GET ALL USERS
    // ============================================

    @Operation(summary = "Get All Users", description = "Mengambil seluruh data User")
    @ApiResponse(responseCode = "200", description = "Berhasil mengambil data User")
    @GetMapping
    public List<UserResponseDTO> findAll() {

        return userService.findAll();
    }

    // ============================================
    // GET USER BY ID
    // ============================================

    @Operation(summary = "Get User By ID", description = "Mengambil detail User berdasarkan ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User ditemukan"),
            @ApiResponse(responseCode = "404", description = "User tidak ditemukan")
    })
    @GetMapping("/{id}")
    public UserResponseDTO findById(
            @PathVariable("id") Long id) {

        return userService.findById(id);
    }

    // ============================================
    // CREATE USER (REGISTRASI)
    // ============================================

    @Operation(summary = "Create User", description = "Mendaftarkan User baru")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User berhasil didaftarkan"),
            @ApiResponse(responseCode = "400", description = "Data input tidak valid"),
            @ApiResponse(responseCode = "409", description = "Email sudah digunakan")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDTO create(
            @Valid @RequestBody UserCreateRequestDTO request) {

        return userService.create(request);
    }

    // ============================================
    // UPDATE USER (PROFIL)
    // ============================================

    @Operation(summary = "Update User", description = "Memperbarui profil User berdasarkan ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User berhasil diperbarui"),
            @ApiResponse(responseCode = "400", description = "Data input tidak valid"),
            @ApiResponse(responseCode = "404", description = "User tidak ditemukan"),
            @ApiResponse(responseCode = "409", description = "Email sudah digunakan oleh User lain")
    })
    @PutMapping("/{id}")
    public UserResponseDTO update(
            @PathVariable("id") Long id,
            @Valid @RequestBody UserUpdateRequestDTO request) {

        return userService.update(id, request);
    }

    // ============================================
    // DELETE USER
    // ============================================

    @Operation(summary = "Delete User", description = "Menghapus data User secara permanen berdasarkan ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User berhasil dihapus"),
            @ApiResponse(responseCode = "404", description = "User tidak ditemukan"),
            @ApiResponse(responseCode = "400", description = "Gagal menghapus, User masih terkait dengan data lain")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable("id") Long id) {

        userService.delete(id);
    }

    // ============================================
    // CHANGE PASSWORD
    // ============================================

    @Operation(summary = "Change Password", description = "Mengubah password User berdasarkan ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password berhasil diubah"),
            @ApiResponse(responseCode = "400", description = "Password lama salah atau konfirmasi password tidak cocok"),
            @ApiResponse(responseCode = "404", description = "User tidak ditemukan")
    })
    @PatchMapping("/{id}/password")
    public void changePassword(
            @PathVariable("id") Long id,
            @Valid @RequestBody ChangePasswordRequestDTO request) {

        userService.changePassword(id, request);
    }

}