package com.j2ee.MovieManageSystem.controller;

import com.j2ee.MovieManageSystem.common.PageResult;
import com.j2ee.MovieManageSystem.common.Result;
import com.j2ee.MovieManageSystem.dto.request.StatusRequest;
import com.j2ee.MovieManageSystem.dto.response.MovieListResponse;
import com.j2ee.MovieManageSystem.entity.UserMovieStatus;
import com.j2ee.MovieManageSystem.service.StatusService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/status")
public class StatusController {

    private final StatusService statusService;

    public StatusController(StatusService statusService) {
        this.statusService = statusService;
    }

    /** 按状态列出当前用户的影视列表（想看/看过） */
    @GetMapping("/movies")
    public Result<PageResult<MovieListResponse>> listMovies(
            @RequestParam String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int size) {
        return Result.ok(statusService.listMoviesByStatus(page, size, status));
    }

    @GetMapping("/{movieId}")
    public Result<UserMovieStatus> getStatus(@PathVariable Long movieId) {
        return Result.ok(statusService.getStatus(movieId));
    }

    @PostMapping
    public Result<?> markStatus(@Valid @RequestBody StatusRequest request) {
        statusService.markStatus(request);
        return Result.ok("标记成功");
    }

    /** 观看热力图 */
    @GetMapping("/heatmap")
    public Result<?> heatmap() {
        return Result.ok(statusService.getWatchHeatmap());
    }
}
