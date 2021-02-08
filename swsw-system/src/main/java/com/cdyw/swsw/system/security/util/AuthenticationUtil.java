package com.cdyw.swsw.system.security.util;

import com.cdyw.swsw.common.domain.entity.user.SysUserEntity;
import com.cdyw.swsw.system.security.config.AuthUser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * @author cdyw
 * @modified jovi
 */
@Component
public class AuthenticationUtil {

    /**
     * 获得当前用户的用户信息(注意作用域是原型，因为每次获取的用户不一样)
     *
     * @return SysUserEntity
     */
    @Scope(value = "prototype")
    @Bean
    public SysUserEntity getCurrentUser() {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        AuthUser u = (AuthUser) token.getPrincipal();
        return u.getUserEntity();
    }
}
