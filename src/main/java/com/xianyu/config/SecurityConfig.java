package com.xianyu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}