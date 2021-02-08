package com.cdyw.swsw.system.security.config;


import com.cdyw.swsw.common.domain.entity.user.SysUserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * 要实现UserDetails接口，这个接口是security提供的
 */
@Component
public class AuthUser implements UserDetails {

    private String username;

    private String password;

    private Integer state;

    private Collection<? extends GrantedAuthority> authorities;

    private SysUserEntity userEntity;

    public AuthUser() {
    }

    public AuthUser(String username, String password, Integer state, Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.password = password;
        this.state = state;
        this.authorities = authorities;
    }

    public AuthUser(String username, String password, Integer state, Collection<? extends GrantedAuthority> authorities, SysUserEntity userEntity) {
        this.username = username;
        this.password = password;
        this.state = state;
        this.authorities = authorities;
        this.userEntity = userEntity;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    // 账户是否未过期
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 账户是否未被锁
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public SysUserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(SysUserEntity userEntity) {
        this.userEntity = userEntity;
    }

    @Override
    public String toString() {
        return "AuthUser{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", state=" + state +
                ", authorities=" + authorities +
                ", userEntity=" + userEntity +
                '}';
    }


}
