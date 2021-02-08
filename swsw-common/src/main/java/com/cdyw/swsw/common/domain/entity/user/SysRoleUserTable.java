package com.cdyw.swsw.common.domain.entity.user;


import lombok.Data;

/**
 * (SysRoleUserTable)表实体类
 *
 * @author cdyw
 */
@SuppressWarnings("serial")
@Data
public class SysRoleUserTable {

    private int id;

    private Integer roleId;

    private Integer userId;
}