package com.j2ee.MovieManageSystem.service;

import com.j2ee.MovieManageSystem.common.PageResult;
import com.j2ee.MovieManageSystem.dto.request.StatusRequest;
import com.j2ee.MovieManageSystem.dto.response.MovieListResponse;
import com.j2ee.MovieManageSystem.entity.UserMovieStatus;

/**
 * 用户影视状态服务接口
 */
public interface StatusService {

    UserMovieStatus getStatus(Long movieId);

    void markStatus(StatusRequest request);

    /** 按状态列出当前用户的影视（想看/看过） */
    PageResult<MovieListResponse> listMoviesByStatus(int page, int size, String status);

    /** 观看热力图数据 */
    java.util.List<java.util.Map<String, Object>> getWatchHeatmap();
}
