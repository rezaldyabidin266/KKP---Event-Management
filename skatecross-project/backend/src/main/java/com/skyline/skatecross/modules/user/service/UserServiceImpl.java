package com.skyline.skatecross.modules.user.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.skyline.skatecross.modules.role.entity.Role;
import com.skyline.skatecross.modules.role.repository.RoleRepository;
import com.skyline.skatecross.modules.user.dto.ChangePasswordRequestDTO;
import com.skyline.skatecross.modules.user.dto.UserCreateRequestDTO;
import com.skyline.skatecross.modules.user.dto.UserResponseDTO;
import com.skyline.skatecross.modules.user.dto.UserUpdateRequestDTO;
import com.skyline.skatecross.modules.user.entity.User;
import com.skyline.skatecross.modules.user.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    // Constructor Injection (Best Practice)
    public UserServiceImpl(UserRepository userRepository, 
                           RoleRepository roleRepository, 
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> findAll() {
        return userRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User tidak ditemukan"));
        
        return mapToResponseDTO(user);
    }

    @Override
    @Transactional
    public UserResponseDTO create(UserCreateRequestDTO request) {
        // UPDATE DI SINI: Gunakan existsByEmailIgnoreCase
        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email sudah digunakan");
        }

        // Ambil data Role dari database
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role tidak ditemukan"));

        // Mapping DTO ke Entity
        User user = new User();
        user.setRole(role);
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setActive(true); // Default active
        
        // Hash Password sebelum disimpan
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        user.setPasswordHash(hashedPassword);

        // Simpan ke database
        User savedUser = userRepository.save(user);

        return mapToResponseDTO(savedUser);
    }

    @Override
    @Transactional
    public UserResponseDTO update(Long id, UserUpdateRequestDTO request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User tidak ditemukan"));

        // UPDATE DI SINI: Gunakan equalsIgnoreCase dan existsByEmailIgnoreCase
        if (!user.getEmail().equalsIgnoreCase(request.getEmail()) && 
             userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email sudah digunakan oleh user lain");
        }

        // Update Role jika ID Role berbeda
        if (!user.getRole().getId().equals(request.getRoleId())) {
            Role role = roleRepository.findById(request.getRoleId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role tidak ditemukan"));
            user.setRole(role);
        }

        // Update data lainnya
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setActive(request.isActive());

        User updatedUser = userRepository.save(user);

        return mapToResponseDTO(updatedUser);
    }
    @Override
    @Transactional
    public void changePassword(Long id, ChangePasswordRequestDTO request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User tidak ditemukan"));

        // 1. Cek apakah password lama sesuai dengan yang di database
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password lama salah");
        }

        // 2. Cek apakah password baru dan konfirmasi cocok
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Konfirmasi password baru tidak cocok");
        }

        // 3. Hash password baru dan simpan
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User tidak ditemukan"));
        
        // Hard Delete (Langsung hapus dari database)
        userRepository.delete(user);
        
        // Catatan: Jika ingin Soft Delete, gunakan kode ini sebagai gantinya:
        // user.setActive(false);
        // userRepository.save(user);
    }

    // ============================================
    // HELPER METHOD UNTUK MAPPING ENTITY KE DTO
    // ============================================
    
    private UserResponseDTO mapToResponseDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setActive(user.isActive());
        dto.setCreatedAt(user.getCreatedAt());
        
        // Mengambil nama role dari relasi
        if (user.getRole() != null) {
            dto.setRoleName(user.getRole().getName()); // Pastikan Entity Role memiliki getter getName()
        }
        
        return dto;
    }
}