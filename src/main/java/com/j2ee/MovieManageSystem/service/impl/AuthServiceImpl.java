package com.j2ee.MovieManageSystem.service.impl;

import com.j2ee.MovieManageSystem.common.BusinessException;
import com.j2ee.MovieManageSystem.dto.request.LoginRequest;
import com.j2ee.MovieManageSystem.dto.request.RegisterRequest;
import com.j2ee.MovieManageSystem.dto.response.LoginResponse;
import com.j2ee.MovieManageSystem.entity.User;
import com.j2ee.MovieManageSystem.mapper.UserMapper;
import com.j2ee.MovieManageSystem.service.AuthService;
import com.j2ee.MovieManageSystem.util.JwtUtil;
import com.j2ee.MovieManageSystem.util.PasswordUtil;
import org.springframework.stereotype.Service;

/**
 * 认证服务实现
 */
@Service
public class AuthServiceImpl implements AuthService {

    private static final String ADMIN_SECRET = "阿拉霍洞开";

    private final UserMapper userMapper;
    private final PasswordUtil passwordUtil;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UserMapper userMapper, PasswordUtil passwordUtil, JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.passwordUtil = passwordUtil;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void register(RegisterRequest request) {
        // 管理员注册必须验证暗号
        if ("admin".equals(request.getRole())) {
            if (request.getSecret() == null || !ADMIN_SECRET.equals(request.getSecret())) {
                throw new BusinessException(400, "管理员暗号错误，无法注册");
            }
        }

        User existing = userMapper.findByUsername(request.getUsername());
        if (existing != null) {
            throw new BusinessException(400, "用户名已存在");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordUtil.encode(request.getPassword()));
        user.setRole(request.getRole());
        userMapper.insert(user);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userMapper.findByUsername(request.getUsername());
        if (user == null) {
            throw new BusinessException(400, "用户名或密码错误");
        }

        if ("disabled".equals(user.getStatus())) {
            throw new BusinessException(403, "账号已被禁用，请联系管理员");
        }

        if (!passwordUtil.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(400, "用户名或密码错误");
        }

        // 管理员登录必须验证暗号
        if ("admin".equals(user.getRole())) {
            if (request.getSecret() == null || !ADMIN_SECRET.equals(request.getSecret())) {
                throw new BusinessException(400, "管理员暗号错误，无法登录");
            }
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        return new LoginResponse(token, user.getId(), user.getUsername(), user.getRole());
    }
}
