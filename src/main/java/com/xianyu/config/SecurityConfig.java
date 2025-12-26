package com.xianyu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(
                                "/", "/index.html", "/login.html", "/register.html",
                                "/item-edit.html", "/item-detail.html", "/my-orders.html", "/user-center.html",
                                "/favicon.ico",

                                // 静态资源：static下以及所有常见扩展名（全匹配保险）
                                "/css/**", "/js/**", "/images/**", "/static/**",

                                // 允许匿名访问的API
                                "/api/users/login", "/api/users/register"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                // 不使用Spring Security自带的登录页
                .formLogin(form -> form.disable());
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}