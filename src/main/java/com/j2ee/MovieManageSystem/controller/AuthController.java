package com.j2ee.MovieManageSystem.controller;

import com.j2ee.MovieManageSystem.common.Result;
import com.j2ee.MovieManageSystem.dto.request.LoginRequest;
import com.j2ee.MovieManageSystem.dto.request.RegisterRequest;
import com.j2ee.MovieManageSystem.dto.response.LoginResponse;
import com.j2ee.MovieManageSystem.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<?> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return Result.ok("注册成功");
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return Result.ok("登录成功", response);
    }
}
