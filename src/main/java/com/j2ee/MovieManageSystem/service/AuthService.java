package com.j2ee.MovieManageSystem.service;

import com.j2ee.MovieManageSystem.dto.request.LoginRequest;
import com.j2ee.MovieManageSystem.dto.request.RegisterRequest;
import com.j2ee.MovieManageSystem.dto.response.LoginResponse;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 用户注册
     */
    void register(RegisterRequest request);

    /**
     * 用户登录
     */
    LoginResponse login(LoginRequest request);
}
