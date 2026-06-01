package com.j2ee.MovieManageSystem.controller;

import com.j2ee.MovieManageSystem.common.PageResult;
import com.j2ee.MovieManageSystem.common.Result;
import com.j2ee.MovieManageSystem.dto.response.MovieListResponse;
import com.j2ee.MovieManageSystem.service.AdminService;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员 - 影视管理
 */
@RestController
@RequestMapping("/api/admin/movies")
public class AdminMovieController {

    private final AdminService adminService;

    public AdminMovieController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping
    public Result<PageResult<MovieListResponse>> listMovies(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        return Result.ok(adminService.listMovies(page, size, keyword));
    }

    @DeleteMapping("/{id}")
    public Result<?> deleteMovie(@PathVariable Long id) {
        adminService.deleteMovie(id);
        return Result.ok("删除成功");
    }
}
