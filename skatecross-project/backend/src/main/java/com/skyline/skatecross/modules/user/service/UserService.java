package com.skyline.skatecross.modules.user.service;

import java.util.List;

import com.skyline.skatecross.modules.user.dto.ChangePasswordRequestDTO;
import com.skyline.skatecross.modules.user.dto.UserCreateRequestDTO;
import com.skyline.skatecross.modules.user.dto.UserResponseDTO;
import com.skyline.skatecross.modules.user.dto.UserUpdateRequestDTO;

public interface UserService {
    
    List<UserResponseDTO> findAll();
    
    UserResponseDTO findById(Long id);
    
    UserResponseDTO create(UserCreateRequestDTO request);
    
    UserResponseDTO update(Long id, UserUpdateRequestDTO request);
    
    void changePassword(Long id, ChangePasswordRequestDTO request);
    
    void delete(Long id);
}