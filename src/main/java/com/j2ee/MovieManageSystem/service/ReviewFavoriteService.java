package com.j2ee.MovieManageSystem.service;

import com.j2ee.MovieManageSystem.common.PageResult;
import com.j2ee.MovieManageSystem.dto.response.ReviewResponse;

import java.util.List;

public interface ReviewFavoriteService {

    void addFavorite(Long reviewId);

    void removeFavorite(Long reviewId);

    /** 当前用户收藏的影评ID列表 */
    List<Long> getMyFavoriteIds();

    /** 我收藏的影评列表 */
    PageResult<ReviewResponse> listMyFavorites(int page, int size);
}
