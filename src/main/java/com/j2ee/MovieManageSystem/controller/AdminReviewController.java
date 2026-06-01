package com.j2ee.MovieManageSystem.controller;

import com.j2ee.MovieManageSystem.common.PageResult;
import com.j2ee.MovieManageSystem.common.Result;
import com.j2ee.MovieManageSystem.dto.response.ReviewResponse;
import com.j2ee.MovieManageSystem.service.AdminService;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员 - 评论管理
 */
@RestController
@RequestMapping("/api/admin/reviews")
public class AdminReviewController {

    private final AdminService adminService;

    public AdminReviewController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping
    public Result<PageResult<ReviewResponse>> listReviews(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        return Result.ok(adminService.listReviews(page, size, keyword));
    }

    @DeleteMapping("/{id}")
    public Result<?> deleteReview(@PathVariable Long id) {
        adminService.deleteReview(id);
        return Result.ok("删除成功");
    }
}
