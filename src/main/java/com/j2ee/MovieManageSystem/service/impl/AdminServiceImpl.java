package com.j2ee.MovieManageSystem.service.impl;

import com.j2ee.MovieManageSystem.common.BusinessException;
import com.j2ee.MovieManageSystem.common.PageResult;
import com.j2ee.MovieManageSystem.dto.response.MovieListResponse;
import com.j2ee.MovieManageSystem.dto.response.ReviewResponse;
import com.j2ee.MovieManageSystem.dto.response.StatsResponse;
import com.j2ee.MovieManageSystem.entity.User;
import com.j2ee.MovieManageSystem.interceptor.CurrentUser;
import com.j2ee.MovieManageSystem.mapper.MovieMapper;
import com.j2ee.MovieManageSystem.mapper.ReviewMapper;
import com.j2ee.MovieManageSystem.mapper.UserMapper;
import com.j2ee.MovieManageSystem.service.AdminService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 管理员服务实现
 */
@Service
public class AdminServiceImpl implements AdminService {

    private final UserMapper userMapper;
    private final MovieMapper movieMapper;
    private final ReviewMapper reviewMapper;

    public AdminServiceImpl(UserMapper userMapper, MovieMapper movieMapper, ReviewMapper reviewMapper) {
        this.userMapper = userMapper;
        this.movieMapper = movieMapper;
        this.reviewMapper = reviewMapper;
    }

    private void checkAdmin() {
        if (!"admin".equals(CurrentUser.getRole())) {
            throw new BusinessException(403, "需要管理员权限");
        }
    }

    // ==================== 用户管理 ====================

    @Override
    public PageResult<User> listUsers(int page, int size, String keyword, String role, String status) {
        checkAdmin();
        List<User> all = userMapper.selectPage(keyword, role, status);
        int offset = (page - 1) * size;
        int toIndex = Math.min(offset + size, all.size());
        List<User> pageList = (offset >= all.size()) ? List.of() : all.subList(offset, toIndex);
        // 不返回密码
        pageList.forEach(u -> u.setPassword(null));
        return PageResult.of(all.size(), page, size, pageList);
    }

    @Override
    @Transactional
    public void updateUserStatus(Long userId, String status) {
        checkAdmin();
        User user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException(404, "用户不存在");
        user.setStatus(status);
        userMapper.updateRoleStatus(user);
    }

    @Override
    @Transactional
    public void updateUserRole(Long userId, String role) {
        checkAdmin();
        User user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException(404, "用户不存在");
        user.setRole(role);
        userMapper.updateRoleStatus(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        checkAdmin();
        if (userMapper.selectById(userId) == null) {
            throw new BusinessException(404, "用户不存在");
        }
        userMapper.deleteById(userId);
    }

    // ==================== 影视管理 ====================

    @Override
    public PageResult<MovieListResponse> listMovies(int page, int size, String keyword) {
        checkAdmin();
        List<MovieMapper.MovieDetail> all = movieMapper.selectPage(keyword, null, null, null, null, null, null, null);
        int offset = (page - 1) * size;
        int toIndex = Math.min(offset + size, all.size());
        List<MovieMapper.MovieDetail> pageList = (offset >= all.size()) ? List.of() : all.subList(offset, toIndex);

        List<MovieListResponse> list = pageList.stream().map(d -> {
            MovieListResponse resp = new MovieListResponse();
            resp.setId(d.getId());
            resp.setTitleCn(d.getTitleCn());
            resp.setTitleEn(d.getTitleEn());
            resp.setPosterUrl(d.getPosterUrl());
            resp.setDirector(d.getDirector());
            resp.setGenre(d.getGenre());
            resp.setYear(d.getYear());
            resp.setCountry(d.getCountry());
            resp.setDuration(d.getDuration());
            resp.setAvgRating(d.getAvgRating());
            resp.setRatingCount(d.getRatingCount());
            resp.setWatchedCount(d.getWatchedCount());
            resp.setFavoriteCount(d.getFavoriteCount());
            resp.setPublisherId(d.getPublisherId());
            resp.setPublisherName(d.getPublisherName());
            resp.setCreatedAt(d.getCreatedAt());
            return resp;
        }).collect(Collectors.toList());

        return PageResult.of(all.size(), page, size, list);
    }

    @Override
    @Transactional
    public void deleteMovie(Long movieId) {
        checkAdmin();
        if (movieMapper.selectById(movieId) == null) {
            throw new BusinessException(404, "影视剧不存在");
        }
        movieMapper.deleteById(movieId);
    }

    // ==================== 评论管理 ====================

    @Override
    public PageResult<ReviewResponse> listReviews(int page, int size, String keyword) {
        checkAdmin();
        List<ReviewMapper.ReviewWithUser> all = reviewMapper.selectAllPage(keyword);
        int offset = (page - 1) * size;
        int toIndex = Math.min(offset + size, all.size());
        List<ReviewMapper.ReviewWithUser> pageList = (offset >= all.size()) ? List.of() : all.subList(offset, toIndex);

        List<ReviewResponse> list = pageList.stream().map(r -> {
            ReviewResponse resp = new ReviewResponse();
            resp.setId(r.getId());
            resp.setUserId(r.getUserId());
            resp.setUsername(r.getUsername());
            resp.setRating(r.getRating());
            resp.setContent(r.getContent());
            resp.setReply(r.getReply());
            resp.setReplyAt(r.getReplyAt());
            resp.setCreatedAt(r.getCreatedAt());
            resp.setUpdatedAt(r.getUpdatedAt());
            return resp;
        }).collect(Collectors.toList());

        return PageResult.of(all.size(), page, size, list);
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId) {
        checkAdmin();
        if (reviewMapper.selectById(reviewId) == null) {
            throw new BusinessException(404, "影评不存在");
        }
        reviewMapper.deleteById(reviewId);
    }

    // ==================== 统计 ====================

    @Override
    public StatsResponse getUserStats() {
        checkAdmin();
        return buildStats(userMapper.countByDayLast30Days());
    }

    @Override
    public StatsResponse getMovieStats() {
        checkAdmin();
        return buildStats(movieMapper.countByDayLast30Days());
    }

    /**
     * 将按日聚合的查询结果转换为 StatsResponse（补齐最近30天）
     */
    private StatsResponse buildStats(List<Map<String, Object>> rows) {
        // 按 label（日期）建立映射
        Map<String, Integer> map = new LinkedHashMap<>();
        for (Map<String, Object> row : rows) {
            String label = row.get("label").toString();
            int cnt = ((Number) row.get("cnt")).intValue();
            map.put(label, cnt);
        }

        // 补齐最近30天
        List<String> labels = new ArrayList<>();
        List<Integer> values = new ArrayList<>();
        java.time.LocalDate today = java.time.LocalDate.now();
        for (int i = 29; i >= 0; i--) {
            String day = today.minusDays(i).toString();
            labels.add(day.substring(5)); // "MM-DD"
            values.add(map.getOrDefault(day, 0));
        }

        return new StatsResponse(labels, values);
    }
}
