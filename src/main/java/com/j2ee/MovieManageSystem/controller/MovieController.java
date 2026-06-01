package com.j2ee.MovieManageSystem.controller;

import com.j2ee.MovieManageSystem.common.PageResult;
import com.j2ee.MovieManageSystem.common.Result;
import com.j2ee.MovieManageSystem.dto.request.MovieRequest;
import com.j2ee.MovieManageSystem.dto.response.MovieDetailResponse;
import com.j2ee.MovieManageSystem.dto.response.MovieListResponse;
import com.j2ee.MovieManageSystem.service.MovieService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 影视剧控制器
 */
@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    /**
     * 影视列表（分页+搜索+筛选）
     */
    @GetMapping
    public Result<PageResult<MovieListResponse>> listMovies(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String filterMode,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) Long publisherId,
            @RequestParam(required = false) String sort) {
        PageResult<MovieListResponse> result = movieService.listMovies(page, size, keyword, genre, language, filterMode, year, country, publisherId, sort);
        return Result.ok(result);
    }

    /**
     * 影视详情
     */
    @GetMapping("/{id}")
    public Result<MovieDetailResponse> getDetail(@PathVariable Long id) {
        MovieDetailResponse detail = movieService.getMovieDetail(id);
        return Result.ok(detail);
    }

    /**
     * 新增影视剧（发布者）
     */
    @PostMapping
    public Result<Long> create(@Valid @RequestBody MovieRequest request) {
        Long id = movieService.createMovie(request);
        return Result.ok("添加成功", id);
    }

    /**
     * 编辑影视剧（发布者本人）
     */
    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @Valid @RequestBody MovieRequest request) {
        movieService.updateMovie(id, request);
        return Result.ok("更新成功");
    }

    /**
     * 删除影视剧（发布者本人/管理员）
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return Result.ok("删除成功");
    }
}
