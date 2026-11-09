
package com.skyline.skatecross.modules.role.mapper;

import org.springframework.stereotype.Component;

import com.skyline.skatecross.modules.role.dto.RoleAccessResponse;
import com.skyline.skatecross.modules.role.entity.RoleAccess;

@Component
public class RoleAccessMapper {

    public RoleAccessResponse toResponse(RoleAccess entity) {

        return new RoleAccessResponse(
                entity.getId(),
                entity.getRole().getId(),
                entity.getPageKey(),
                entity.isCanView(),
                entity.isCanCreate(),
                entity.isCanEdit(),
                entity.isCanDelete(),
                entity.isCanApprove());
    }
}
