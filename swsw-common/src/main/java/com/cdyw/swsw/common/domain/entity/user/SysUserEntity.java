package com.cdyw.swsw.common.domain.entity.user;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * (SysUserTable)表实体类
 *
 * @author cdyw
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysUserEntity {
    private Integer userId;

    private String userName;

    private String nickName;

    private String passWord;

    private Integer state;

    private String description;

    private List<String> roleList;

    public SysUserEntity(Integer userId, String userName, String passWord, Integer state, String description) {
        this.userId = userId;
        this.userName = userName;
        this.passWord = passWord;
        this.state = state;
        this.description = description;
    }
}
