package com.cdyw.swsw.common.domain.entity.user;

import lombok.Data;

/**
 * 角色和鉴权资源的VO
 * 如：角色和用户VO
 * 角色和API
 * 角色和前端菜单
 * 他们都可以使用该VO来转换并展现给前端
 *
 * @author liudong
 */
@Data
public class SysRoleAndPermissionVo {
    private String id;
    private String name;
    private String roleId;
    private String pid;
}
