
package com.skyline.skatecross.modules.role.mapper;

import org.springframework.stereotype.Component;

import com.skyline.skatecross.modules.role.dto.RoleResponse;
import com.skyline.skatecross.modules.role.entity.Role;

@Component
public class RoleMapper {

    public RoleResponse toResponse(Role role) {

        return new RoleResponse(
                role.getId(),
                role.getCode(),
                role.getName(),
                role.getDescription(),
                role.isActive());
    }
}
