package com.j2ee.MovieManageSystem.service;

import com.j2ee.MovieManageSystem.common.PageResult;
import com.j2ee.MovieManageSystem.dto.response.MovieListResponse;

/**
 * 榜单服务接口
 */
public interface RankingService {

    /** 高分榜：avg_rating >= 7.0 且评分人数 >= 3，按评分降序 */
    PageResult<MovieListResponse> highRating(int page, int size);

    /** 收藏榜：按收藏数降序 */
    PageResult<MovieListResponse> favorite(int page, int size);

    /** 新片榜：按上映年份降序，相同按创建时间降序 */
    PageResult<MovieListResponse> latest(int page, int size);

    /** 看过最多榜：按标记"看过"的用户数降序 */
    PageResult<MovieListResponse> mostWatched(int page, int size);
}
