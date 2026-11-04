
package com.skyline.skatecross.modules.role.service;

import java.util.List;

import com.skyline.skatecross.modules.role.dto.RoleRequest;
import com.skyline.skatecross.modules.role.dto.RoleResponse;

public interface RoleService {

    List<RoleResponse> findAll();

    RoleResponse findById(Long id);

    RoleResponse create(RoleRequest request);

    RoleResponse update(Long id, RoleRequest request);

    RoleResponse setActive(Long id, boolean active);
}
