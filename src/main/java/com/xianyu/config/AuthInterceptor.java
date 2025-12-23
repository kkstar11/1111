package com.xianyu.config;

import com.xianyu.entity.User;
import com.xianyu.util.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;

/**
 * 简单基于 Session 的登录拦截器，未登录返回统一 Result。
 */
public class AuthInterceptor implements HandlerInterceptor {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Object current = request.getSession().getAttribute("currentUser");
        if (current instanceof User) {
            return true;
        }
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(MAPPER.writeValueAsString(Result.failure("unauthorized")));
        return false;
    }
}

