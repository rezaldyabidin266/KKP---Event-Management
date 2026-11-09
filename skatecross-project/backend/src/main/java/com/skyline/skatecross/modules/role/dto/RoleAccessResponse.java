
package com.skyline.skatecross.modules.role.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response permission berdasarkan Role dan Page Key")
public class RoleAccessResponse {

    private final Long id;
    private final Long roleId;
    private final String pageKey;

    private final boolean canView;
    private final boolean canCreate;
    private final boolean canEdit;
    private final boolean canDelete;
    private final boolean canApprove;

    public RoleAccessResponse(
            Long id,
            Long roleId,
            String pageKey,
            boolean canView,
            boolean canCreate,
            boolean canEdit,
            boolean canDelete,
            boolean canApprove) {

        this.id = id;
        this.roleId = roleId;
        this.pageKey = pageKey;
        this.canView = canView;
        this.canCreate = canCreate;
        this.canEdit = canEdit;
        this.canDelete = canDelete;
        this.canApprove = canApprove;
    }

    public Long getId() {
        return id;
    }

    public Long getRoleId() {
        return roleId;
    }

    public String getPageKey() {
        return pageKey;
    }

    public boolean getCanView() {
        return canView;
    }

    public boolean getCanCreate() {
        return canCreate;
    }

    public boolean getCanEdit() {
        return canEdit;
    }

    public boolean getCanDelete() {
        return canDelete;
    }

    public boolean getCanApprove() {
        return canApprove;
    }
}
