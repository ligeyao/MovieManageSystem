package com.j2ee.MovieManageSystem.controller;

import com.j2ee.MovieManageSystem.common.Result;
import com.j2ee.MovieManageSystem.dto.response.StatsResponse;
import com.j2ee.MovieManageSystem.service.AdminService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理员 - 统计图表
 */
@RestController
@RequestMapping("/api/admin/stats")
public class AdminStatsController {

    private final AdminService adminService;

    public AdminStatsController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    public Result<StatsResponse> userStats() {
        return Result.ok(adminService.getUserStats());
    }

    @GetMapping("/movies")
    public Result<StatsResponse> movieStats() {
        return Result.ok(adminService.getMovieStats());
    }
}
