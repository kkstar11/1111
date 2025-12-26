package com.xianyu.security;

import com.xianyu.service.UserService;
import com.xianyu.vo.UserVO;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {
    private final UserService userService;
    public MyUserDetailsService(UserService userService) {
        this.userService = userService;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserVO user = userService.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("用户不存在"));
        return new MyUserDetails(user);
    }
}