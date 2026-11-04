
package com.skyline.skatecross.modules.role.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class RoleRequest {

    @NotBlank(message = "Kode role wajib diisi")
    @Size(max = 50, message = "Kode role maksimal 50 karakter")
    private String code;

    @NotBlank(message = "Nama role wajib diisi")
    @Size(max = 120, message = "Nama role maksimal 120 karakter")
    private String name;

    private String description;

    public RoleRequest() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
