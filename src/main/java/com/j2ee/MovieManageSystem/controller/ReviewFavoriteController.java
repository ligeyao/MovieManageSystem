package com.j2ee.MovieManageSystem.controller;

import com.j2ee.MovieManageSystem.common.PageResult;
import com.j2ee.MovieManageSystem.common.Result;
import com.j2ee.MovieManageSystem.dto.response.ReviewResponse;
import com.j2ee.MovieManageSystem.service.ReviewFavoriteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/review-favorites")
public class ReviewFavoriteController {

    private final ReviewFavoriteService service;

    public ReviewFavoriteController(ReviewFavoriteService service) { this.service = service; }

    @GetMapping("/ids")
    public Result<List<Long>> getMyFavoriteIds() {
        return Result.ok(service.getMyFavoriteIds());
    }

    @GetMapping
    public Result<PageResult<ReviewResponse>> listMyFavorites(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.ok(service.listMyFavorites(page, size));
    }

    @PostMapping
    public Result<?> add(@RequestBody Map<String, Long> body) {
        service.addFavorite(body.get("reviewId"));
        return Result.ok("收藏成功");
    }

    @DeleteMapping("/{reviewId}")
    public Result<?> remove(@PathVariable Long reviewId) {
        service.removeFavorite(reviewId);
        return Result.ok("已取消");
    }
}
