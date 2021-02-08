package com.cdyw.swsw.common.domain.entity.user;


import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * (SysRoleTable)表实体类
 *
 * @author cdyw
 */
@Data
@NoArgsConstructor
public class SysRoleTable {
    private Integer roleId;

    private String roleName;

    private String description;

    public SysRoleTable(Integer roleId, String roleName) {
        this.roleId = roleId;
        this.tranRoleName(roleName);
    }

    public SysRoleTable(Integer roleId, String roleName, String description) {
        this.roleId = roleId;
        this.tranRoleName(roleName);
        this.description = description;
    }

    private void tranRoleName(String roleName) {
        if (roleName.indexOf("ROLE_") == -1) {
            this.roleName = "ROLE_" + roleName;
        } else {
            this.roleName = roleName;
        }
    }
}

