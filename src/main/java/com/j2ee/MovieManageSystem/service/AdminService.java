package com.j2ee.MovieManageSystem.service;

import com.j2ee.MovieManageSystem.common.PageResult;
import com.j2ee.MovieManageSystem.dto.response.MovieListResponse;
import com.j2ee.MovieManageSystem.dto.response.ReviewResponse;
import com.j2ee.MovieManageSystem.dto.response.StatsResponse;
import com.j2ee.MovieManageSystem.entity.User;

/**
 * 管理员服务接口
 */
public interface AdminService {

    // ---- 用户管理 ----
    PageResult<User> listUsers(int page, int size, String keyword, String role, String status);
    void updateUserStatus(Long userId, String status);
    void updateUserRole(Long userId, String role);
    void deleteUser(Long userId);

    // ---- 影视管理 ----
    PageResult<MovieListResponse> listMovies(int page, int size, String keyword);
    void deleteMovie(Long movieId);

    // ---- 评论管理 ----
    PageResult<ReviewResponse> listReviews(int page, int size, String keyword);
    void deleteReview(Long reviewId);

    // ---- 统计 ----
    StatsResponse getUserStats();
    StatsResponse getMovieStats();
}
