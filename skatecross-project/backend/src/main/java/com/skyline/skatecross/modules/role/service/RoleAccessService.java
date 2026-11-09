
package com.skyline.skatecross.modules.role.service;

import java.util.List;

import com.skyline.skatecross.modules.role.dto.RoleAccessRequest;
import com.skyline.skatecross.modules.role.dto.RoleAccessResponse;

public interface RoleAccessService {

    List<RoleAccessResponse> findAllByRoleId(Long roleId);

    RoleAccessResponse findByPageKey(
            Long roleId,
            String pageKey);

    RoleAccessResponse upsert(
            Long roleId,
            String pageKey,
            RoleAccessRequest request);

    void delete(
            Long roleId,
            String pageKey);
}
