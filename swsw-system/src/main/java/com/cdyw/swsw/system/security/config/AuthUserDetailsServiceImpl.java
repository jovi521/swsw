package com.cdyw.swsw.system.security.config;


import com.cdyw.swsw.common.domain.entity.user.SysUserEntity;
import com.cdyw.swsw.system.service.user.SysRoleTableService;
import com.cdyw.swsw.system.service.user.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 要实现UserDetailsService接口，这个接口是security提供的
 *
 * @author liudong
 * @modified jovi
 */
@Service
public class AuthUserDetailsServiceImpl implements UserDetailsService {

    private final SysUserService userService;

    private final SysRoleTableService roleService;

    @Autowired
    public AuthUserDetailsServiceImpl(SysUserService userService, SysRoleTableService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    /**
     * 通过账号查找用户、角色的信息
     *
     * @param username String
     * @return UserDetails
     * @throws UsernameNotFoundException Exception
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUserEntity user = userService.getUserByUserName(username);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("%s.这个用户不存在", username));
        } else {
            //查找角色
            List<String> roles = roleService.getRolesByUserName(username);
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            for (String role : roles) {
                authorities.add(new SimpleGrantedAuthority(role));
            }
            String password = user.getPassWord();
            user.setPassWord(null);
            // 给 activity 注入当前流程操作用户信息
//            Authentication.setAuthenticatedUserId(username);
            return new AuthUser(user.getUserName(), password, user.getState(), authorities, user);
        }
    }
}
