package com.j2ee.MovieManageSystem.controller;

import com.j2ee.MovieManageSystem.common.PageResult;
import com.j2ee.MovieManageSystem.common.Result;
import com.j2ee.MovieManageSystem.entity.User;
import com.j2ee.MovieManageSystem.service.AdminService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 管理员 - 用户管理
 */
@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final AdminService adminService;

    public AdminUserController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping
    public Result<PageResult<User>> listUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status) {
        return Result.ok(adminService.listUsers(page, size, keyword, role, status));
    }

    @PutMapping("/{id}")
    public Result<?> updateUser(@PathVariable Long id, @RequestBody Map<String, String> body) {
        if (body.containsKey("status")) {
            adminService.updateUserStatus(id, body.get("status"));
        }
        if (body.containsKey("role")) {
            adminService.updateUserRole(id, body.get("role"));
        }
        return Result.ok("修改成功");
    }

    @DeleteMapping("/{id}")
    public Result<?> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return Result.ok("删除成功");
    }
}
