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
        // 检查用户名是否已存在
        User existing = userMapper.findByUsername(request.getUsername());
        if (existing != null) {
            throw new BusinessException(400, "用户名已存在");
        }

        // 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordUtil.encode(request.getPassword()));
        user.setRole(request.getRole());
        userMapper.insert(user);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        // 查询用户
        User user = userMapper.findByUsername(request.getUsername());
        if (user == null) {
            throw new BusinessException(400, "用户名或密码错误");
        }

        // 检查用户状态
        if ("disabled".equals(user.getStatus())) {
            throw new BusinessException(403, "账号已被禁用，请联系管理员");
        }

        // 校验密码
        if (!passwordUtil.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(400, "用户名或密码错误");
        }

        // 生成 Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        return new LoginResponse(token, user.getId(), user.getUsername(), user.getRole());
    }
}
