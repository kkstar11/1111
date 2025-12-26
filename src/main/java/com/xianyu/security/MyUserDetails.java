package com.xianyu.security;

import com.xianyu.vo.UserVO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class MyUserDetails implements UserDetails {
    private final UserVO user;

    public MyUserDetails(UserVO user) {
        this.user = user;
        System.out.println("[MyUserDetails] 构造，绑定用户 userVO: " + user);
    }

    @Override
    public String getUsername() {
        System.out.println("[MyUserDetails] getUsername() -> " + user.getUsername());
        return user.getUsername();
    }

    @Override
    public String getPassword() {
        System.out.println("[MyUserDetails] getPassword() -> " + user.getPassword());
        return user.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        System.out.println("[MyUserDetails] getAuthorities() -> 空集合");
        return Collections.emptyList();
    }

    @Override
    public boolean isAccountNonExpired() {
        System.out.println("[MyUserDetails] isAccountNonExpired() -> true");
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        System.out.println("[MyUserDetails] isAccountNonLocked() -> true");
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        System.out.println("[MyUserDetails] isCredentialsNonExpired() -> true");
        return true;
    }

    @Override
    public boolean isEnabled() {
        System.out.println("[MyUserDetails] isEnabled() -> true");
        return true;
    }

    public UserVO getUserVO() {
        System.out.println("[MyUserDetails] getUserVO() -> " + user);
        return user;
    }
}