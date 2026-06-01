package com.j2ee.MovieManageSystem.service;

import com.j2ee.MovieManageSystem.common.PageResult;
import com.j2ee.MovieManageSystem.dto.response.MovieListResponse;

/**
 * 收藏服务接口
 */
public interface FavoriteService {

    /**
     * 收藏影视
     */
    void addFavorite(Long movieId);

    /**
     * 取消收藏
     */
    void removeFavorite(Long movieId);

    /**
     * 我的收藏列表（按收藏时间倒序）
     */
    PageResult<MovieListResponse> listMyFavorites(int page, int size);
}
