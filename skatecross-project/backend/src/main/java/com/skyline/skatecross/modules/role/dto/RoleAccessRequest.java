
package com.skyline.skatecross.modules.role.dto;

import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request untuk membuat atau memperbarui permission Role")
public class RoleAccessRequest {

    @NotNull(message = "Permission view wajib diisi")
    @Schema(description = "Izin melihat data", example = "true", required = true)
    private Boolean canView;

    @NotNull(message = "Permission create wajib diisi")
    @Schema(description = "Izin membuat data", example = "true", required = true)
    private Boolean canCreate;

    @NotNull(message = "Permission edit wajib diisi")
    @Schema(description = "Izin mengubah data", example = "true", required = true)
    private Boolean canEdit;

    @NotNull(message = "Permission delete wajib diisi")
    @Schema(description = "Izin menghapus data", example = "false", required = true)
    private Boolean canDelete;

    @NotNull(message = "Permission approve wajib diisi")
    @Schema(description = "Izin melakukan approval", example = "false", required = true)
    private Boolean canApprove;

    public Boolean getCanView() {
        return canView;
    }

    public void setCanView(Boolean canView) {
        this.canView = canView;
    }

    public Boolean getCanCreate() {
        return canCreate;
    }

    public void setCanCreate(Boolean canCreate) {
        this.canCreate = canCreate;
    }

    public Boolean getCanEdit() {
        return canEdit;
    }

    public void setCanEdit(Boolean canEdit) {
        this.canEdit = canEdit;
    }

    public Boolean getCanDelete() {
        return canDelete;
    }

    public void setCanDelete(Boolean canDelete) {
        this.canDelete = canDelete;
    }

    public Boolean getCanApprove() {
        return canApprove;
    }

    public void setCanApprove(Boolean canApprove) {
        this.canApprove = canApprove;
    }
}
