package com.j2ee.MovieManageSystem.controller;

import com.j2ee.MovieManageSystem.common.PageResult;
import com.j2ee.MovieManageSystem.common.Result;
import com.j2ee.MovieManageSystem.dto.response.MovieListResponse;
import com.j2ee.MovieManageSystem.service.RankingService;
import org.springframework.web.bind.annotation.*;

/**
 * 榜单控制器
 */
@RestController
@RequestMapping("/api/rankings")
public class RankingController {

    private final RankingService rankingService;

    public RankingController(RankingService rankingService) {
        this.rankingService = rankingService;
    }

    @GetMapping("/high-rating")
    public Result<PageResult<MovieListResponse>> highRating(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return Result.ok(rankingService.highRating(page, size));
    }

    @GetMapping("/favorite")
    public Result<PageResult<MovieListResponse>> favorite(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return Result.ok(rankingService.favorite(page, size));
    }

    @GetMapping("/latest")
    public Result<PageResult<MovieListResponse>> latest(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return Result.ok(rankingService.latest(page, size));
    }

    @GetMapping("/most-watched")
    public Result<PageResult<MovieListResponse>> mostWatched(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return Result.ok(rankingService.mostWatched(page, size));
    }
}
