package com.j2ee.MovieManageSystem.service;

import com.j2ee.MovieManageSystem.common.PageResult;
import com.j2ee.MovieManageSystem.dto.request.ReplyRequest;
import com.j2ee.MovieManageSystem.dto.request.ReviewRequest;
import com.j2ee.MovieManageSystem.dto.response.ReviewResponse;

/**
 * 影评服务接口
 */
public interface ReviewService {

    /**
     * 获取某影视的影评列表
     */
    PageResult<ReviewResponse> listByMovie(Long movieId, int page, int size);

    /**
     * 写影评（必须打分）
     */
    Long createReview(ReviewRequest request);

    /**
     * 修改自己的影评
     */
    void updateReview(Long reviewId, ReviewRequest request);

    /**
     * 删除自己的影评
     */
    void deleteReview(Long reviewId);

    /**
     * 发布者回复影评
     */
    void replyReview(Long reviewId, ReplyRequest request);

    /** 获取当前用户的影评列表 */
    PageResult<ReviewResponse> listMyReviews(int page, int size);
}
