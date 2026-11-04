
package com.skyline.skatecross.modules.role.dto;

import javax.validation.constraints.NotNull;

public class RoleStatusRequest {

    @NotNull(message = "Status aktif wajib diisi")
    private Boolean active;

    public RoleStatusRequest() {
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
