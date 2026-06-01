package com.j2ee.MovieManageSystem.controller;

import com.j2ee.MovieManageSystem.common.PageResult;
import com.j2ee.MovieManageSystem.common.Result;
import com.j2ee.MovieManageSystem.dto.request.ReplyRequest;
import com.j2ee.MovieManageSystem.dto.request.ReviewRequest;
import com.j2ee.MovieManageSystem.dto.response.ReviewResponse;
import com.j2ee.MovieManageSystem.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 影评控制器
 */
@RestController
@RequestMapping("/api")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    /**
     * 获取某影视的影评列表
     */
    @GetMapping("/movies/{movieId}/reviews")
    public Result<PageResult<ReviewResponse>> listByMovie(
            @PathVariable Long movieId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResult<ReviewResponse> result = reviewService.listByMovie(movieId, page, size);
        return Result.ok(result);
    }

    /**
     * 写影评（必须打分）
     */
    @PostMapping("/reviews")
    public Result<Long> create(@Valid @RequestBody ReviewRequest request) {
        Long id = reviewService.createReview(request);
        return Result.ok("影评发表成功", id);
    }

    /**
     * 修改自己的影评
     */
    @PutMapping("/reviews/{id}")
    public Result<?> update(@PathVariable Long id, @Valid @RequestBody ReviewRequest request) {
        reviewService.updateReview(id, request);
        return Result.ok("影评修改成功");
    }

    /**
     * 删除自己的影评
     */
    @DeleteMapping("/reviews/{id}")
    public Result<?> delete(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return Result.ok("影评删除成功");
    }

    /**
     * 发布者回复影评
     */
    @PostMapping("/reviews/{id}/reply")
    public Result<?> reply(@PathVariable Long id, @Valid @RequestBody ReplyRequest request) {
        reviewService.replyReview(id, request);
        return Result.ok("回复成功");
    }

    /** 我的影评 */
    @GetMapping("/reviews/my")
    public Result<PageResult<ReviewResponse>> listMyReviews(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.ok(reviewService.listMyReviews(page, size));
    }
}
