package com.j2ee.MovieManageSystem.service.impl;

import com.j2ee.MovieManageSystem.common.BusinessException;
import com.j2ee.MovieManageSystem.common.PageResult;
import com.j2ee.MovieManageSystem.dto.request.StatusRequest;
import com.j2ee.MovieManageSystem.dto.response.MovieListResponse;
import com.j2ee.MovieManageSystem.entity.UserMovieStatus;
import com.j2ee.MovieManageSystem.interceptor.CurrentUser;
import com.j2ee.MovieManageSystem.mapper.MovieMapper;
import com.j2ee.MovieManageSystem.mapper.UserMovieStatusMapper;
import com.j2ee.MovieManageSystem.service.StatusService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户影视状态服务实现
 * 状态流转规则：未标记 → 想看 → 看过（单向，不可回退）
 * 允许从未标记直接标记"看过"（跳过"想看"）
 */
@Service
public class StatusServiceImpl implements StatusService {

    private final UserMovieStatusMapper statusMapper;
    private final MovieMapper movieMapper;

    public StatusServiceImpl(UserMovieStatusMapper statusMapper, MovieMapper movieMapper) {
        this.statusMapper = statusMapper;
        this.movieMapper = movieMapper;
    }

    @Override
    public UserMovieStatus getStatus(Long movieId) {
        Long userId = CurrentUser.getUserId();
        return statusMapper.selectByUserAndMovie(userId, movieId);
    }

    @Override
    @Transactional
    public void markStatus(StatusRequest request) {
        Long userId = CurrentUser.getUserId();
        Long movieId = request.getMovieId();
        String newStatus = request.getStatus();

        // 检查影视是否存在
        if (movieMapper.selectById(movieId) == null) {
            throw new BusinessException(404, "影视剧不存在");
        }

        UserMovieStatus existing = statusMapper.selectByUserAndMovie(userId, movieId);

        if (existing == null) {
            // 未标记 → 想看 或 未标记 → 看过（跳过"想看"）
            UserMovieStatus ums = new UserMovieStatus();
            ums.setUserId(userId);
            ums.setMovieId(movieId);
            ums.setStatus(newStatus);
            if ("watched".equals(newStatus)) {
                ums.setWatchDate(LocalDateTime.now());
            }
            statusMapper.insert(ums);

            // 如果标记为"看过"，更新影视的 watched_count
            if ("watched".equals(newStatus)) {
                movieMapper.incrementWatchedCount(movieId, 1);
            }
        } else if ("want_to_watch".equals(existing.getStatus()) && "watched".equals(newStatus)) {
            // 想看 → 看过（允许升级）
            existing.setStatus("watched");
            existing.setWatchDate(LocalDateTime.now());
            statusMapper.update(existing);

            // 更新影视的 watched_count +1
            movieMapper.incrementWatchedCount(movieId, 1);
        } else if ("watched".equals(existing.getStatus())) {
            // 看过 → 看过（重复，忽略）
            throw new BusinessException(400, "已标记为看过，不可重复操作");
        } else if ("watched".equals(existing.getStatus()) && "want_to_watch".equals(newStatus)) {
            // 看过 → 想看（不允许回退）
            throw new BusinessException(400, "已看过，不可回退为想看");
        } else if ("want_to_watch".equals(existing.getStatus()) && "want_to_watch".equals(newStatus)) {
            throw new BusinessException(400, "已标记为想看，不可重复操作");
        }
    }

    @Override
    public PageResult<MovieListResponse> listMoviesByStatus(int page, int size, String status) {
        Long userId = CurrentUser.getUserId();
        List<MovieMapper.MovieDetail> all = statusMapper.selectMoviesByUserIdAndStatus(userId, status);

        int offset = (page - 1) * size;
        int toIndex = Math.min(offset + size, all.size());
        List<MovieMapper.MovieDetail> pageList = (offset >= all.size()) ? List.of() : all.subList(offset, toIndex);

        List<MovieListResponse> list = pageList.stream().map(d -> {
            MovieListResponse resp = new MovieListResponse();
            resp.setId(d.getId()); resp.setTitleCn(d.getTitleCn()); resp.setTitleEn(d.getTitleEn());
            resp.setPosterUrl(d.getPosterUrl()); resp.setDirector(d.getDirector());
            resp.setGenre(d.getGenre()); resp.setYear(d.getYear()); resp.setCountry(d.getCountry());
            resp.setDuration(d.getDuration()); resp.setAvgRating(d.getAvgRating());
            resp.setRatingCount(d.getRatingCount()); resp.setWatchedCount(d.getWatchedCount());
            resp.setFavoriteCount(d.getFavoriteCount());
            resp.setPublisherId(d.getPublisherId()); resp.setPublisherName(d.getPublisherName());
            resp.setCreatedAt(d.getCreatedAt());
            return resp;
        }).collect(Collectors.toList());

        return PageResult.of(all.size(), page, size, list);
    }
}
