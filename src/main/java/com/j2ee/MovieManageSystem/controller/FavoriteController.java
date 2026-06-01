package com.j2ee.MovieManageSystem.controller;

import com.j2ee.MovieManageSystem.common.PageResult;
import com.j2ee.MovieManageSystem.common.Result;
import com.j2ee.MovieManageSystem.dto.response.MovieListResponse;
import com.j2ee.MovieManageSystem.service.FavoriteService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 收藏控制器
 */
@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    /**
     * 我的收藏列表
     */
    @GetMapping
    public Result<PageResult<MovieListResponse>> listMyFavorites(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResult<MovieListResponse> result = favoriteService.listMyFavorites(page, size);
        return Result.ok(result);
    }

    /**
     * 收藏影视
     */
    @PostMapping
    public Result<?> addFavorite(@RequestBody Map<String, Long> body) {
        Long movieId = body.get("movieId");
        favoriteService.addFavorite(movieId);
        return Result.ok("收藏成功");
    }

    /**
     * 取消收藏
     */
    @DeleteMapping("/{movieId}")
    public Result<?> removeFavorite(@PathVariable Long movieId) {
        favoriteService.removeFavorite(movieId);
        return Result.ok("已取消收藏");
    }
}
