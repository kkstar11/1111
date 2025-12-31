package com.xianyu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/favicon.ico", "/css/**", "/js/**", "/images/**", "/static/**",
                                "/login.html", "/register.html",
                                "/api/users/login", "/api/users/register"
                        ).permitAll()
                        .requestMatchers(
                                "/item-edit.html",
                                "/api/items", "/api/items/**",
                                "/my-orders.html"
                        ).authenticated()
                        .anyRequest().permitAll()
                )
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .exceptionHandling(eh -> eh
                        .authenticationEntryPoint(
                                (request, response, authException) -> {
                                    String accept = request.getHeader("Accept");
                                    if (accept != null && accept.contains("text/html")) {
                                        response.sendRedirect("/login.html?from=" + request.getRequestURI());
                                    } else {
                                        response.setContentType("application/json;charset=UTF-8");
                                        response.setStatus(401);
                                        response.getWriter().write("{\"error\":\"用户未登录\"}");
                                    }
                                }
                        )
                );
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * PasswordEncoder配置 - 使用明文密码编码器
     * 
     * ⚠️ 警告：此配置仅用于开发测试环境，禁止在生产环境中使用！
     * 
     * NoOpPasswordEncoder不对密码进行任何加密处理，直接使用明文密码进行比对。
     * 这样可以在开发测试时，直接在数据库student_user.password列中填入明文密码（如'123456'），
     * 管理员即可使用明文密码直接登录，便于测试。
     * 
     * 生产环境必须使用BCryptPasswordEncoder或其他安全的密码编码器：
     * return new BCryptPasswordEncoder();
     * 
     * @return NoOpPasswordEncoder实例（仅限开发测试使用）
     */
    @Bean
    @SuppressWarnings("deprecation") // NoOpPasswordEncoder已被标记为过时，但用于测试环境
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}