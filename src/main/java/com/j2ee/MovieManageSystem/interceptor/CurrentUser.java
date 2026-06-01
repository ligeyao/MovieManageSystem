package com.j2ee.MovieManageSystem.interceptor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 当前用户工具类
 * 从 SecurityContext 中获取当前登录用户信息
 */
public class CurrentUser {

    /**
     * 获取当前用户ID
     */
    public static Long getUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Long) {
            return (Long) auth.getPrincipal();
        }
        throw new RuntimeException("未登录或 Token 已过期");
    }

    /**
     * 获取当前用户角色
     */
    public static String getRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getDetails() instanceof JwtAuthFilter.UserInfo info) {
            return info.role();
        }
        throw new RuntimeException("未登录或 Token 已过期");
    }

    /**
     * 获取当前用户名
     */
    public static String getUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getDetails() instanceof JwtAuthFilter.UserInfo info) {
            return info.username();
        }
        throw new RuntimeException("未登录或 Token 已过期");
    }

    /**
     * 获取当前用户完整信息
     */
    public static JwtAuthFilter.UserInfo getUserInfo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getDetails() instanceof JwtAuthFilter.UserInfo info) {
            return info;
        }
        throw new RuntimeException("未登录或 Token 已过期");
    }
}
