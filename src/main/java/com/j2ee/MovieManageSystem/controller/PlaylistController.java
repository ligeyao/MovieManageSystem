package com.j2ee.MovieManageSystem.controller;

import com.j2ee.MovieManageSystem.common.PageResult;
import com.j2ee.MovieManageSystem.common.Result;
import com.j2ee.MovieManageSystem.dto.response.MovieListResponse;
import com.j2ee.MovieManageSystem.entity.Playlist;
import com.j2ee.MovieManageSystem.service.PlaylistService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/playlists")
public class PlaylistController {

    private final PlaylistService playlistService;

    public PlaylistController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    @GetMapping
    public Result<List<Playlist>> listMyPlaylists() {
        return Result.ok(playlistService.listMyPlaylists());
    }

    @PostMapping
    public Result<Playlist> create(@RequestBody Map<String, String> body) {
        Playlist p = playlistService.createPlaylist(body.get("name"), body.get("description"));
        return Result.ok("创建成功", p);
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody Map<String, String> body) {
        playlistService.updatePlaylist(id, body.get("name"), body.get("description"));
        return Result.ok("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        playlistService.deletePlaylist(id);
        return Result.ok("删除成功");
    }

    /** 片单内影视列表 */
    @GetMapping("/{id}/movies")
    public Result<PageResult<MovieListResponse>> listMovies(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return Result.ok(playlistService.listMoviesInPlaylist(id, page, size));
    }

    /** 添加影视到片单 */
    @PostMapping("/{id}/movies")
    public Result<?> addMovie(@PathVariable Long id, @RequestBody Map<String, Long> body) {
        playlistService.addMovieToPlaylist(id, body.get("movieId"));
        return Result.ok("已加入片单");
    }

    /** 从片单移除影视 */
    @DeleteMapping("/{id}/movies/{movieId}")
    public Result<?> removeMovie(@PathVariable Long id, @PathVariable Long movieId) {
        playlistService.removeMovieFromPlaylist(id, movieId);
        return Result.ok("已移除");
    }

    /** 获取某影视所在的当前用户片单ID列表 */
    @GetMapping("/by-movie/{movieId}")
    public Result<List<Long>> getMyPlaylistIdsByMovie(@PathVariable Long movieId) {
        return Result.ok(playlistService.getMyPlaylistIdsByMovie(movieId));
    }
}
