package com.j2ee.MovieManageSystem.service.impl;

import com.j2ee.MovieManageSystem.common.BusinessException;
import com.j2ee.MovieManageSystem.common.PageResult;
import com.j2ee.MovieManageSystem.dto.request.ReplyRequest;
import com.j2ee.MovieManageSystem.dto.request.ReviewRequest;
import com.j2ee.MovieManageSystem.dto.response.ReviewResponse;
import com.j2ee.MovieManageSystem.entity.Review;
import com.j2ee.MovieManageSystem.interceptor.CurrentUser;
import com.j2ee.MovieManageSystem.mapper.MovieMapper;
import com.j2ee.MovieManageSystem.mapper.ReviewMapper;
import com.j2ee.MovieManageSystem.service.BadgeService;
import com.j2ee.MovieManageSystem.service.ReviewService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 影评服务实现
 */
@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewMapper reviewMapper;
    private final MovieMapper movieMapper;
    private final BadgeService badgeService;

    public ReviewServiceImpl(ReviewMapper reviewMapper, MovieMapper movieMapper, BadgeService badgeService) {
        this.reviewMapper = reviewMapper;
        this.movieMapper = movieMapper;
        this.badgeService = badgeService;
    }

    @Override
    public PageResult<ReviewResponse> listByMovie(Long movieId, int page, int size) {
        List<ReviewMapper.ReviewWithUser> reviews = reviewMapper.selectByMovieId(movieId);

        int offset = (page - 1) * size;
        int toIndex = Math.min(offset + size, reviews.size());
        List<ReviewMapper.ReviewWithUser> pageList;
        if (offset >= reviews.size()) {
            pageList = List.of();
        } else {
            pageList = reviews.subList(offset, toIndex);
        }

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

        return PageResult.of(reviews.size(), page, size, list);
    }

    @Override
    @Transactional
    public Long createReview(ReviewRequest request) {
        Long userId = CurrentUser.getUserId();

        // 检查是否已写过影评
        Review existing = reviewMapper.selectByUserAndMovie(userId, request.getMovieId());
        if (existing != null) {
            throw new BusinessException(400, "您已经对该影视剧发表过影评，请使用修改功能");
        }

        Review review = new Review();
        review.setUserId(userId);
        review.setMovieId(request.getMovieId());
        review.setRating(request.getRating());
        review.setContent(request.getContent());
        reviewMapper.insert(review);

        // 重新计算该影视的平均评分
        recalcMovieRating(request.getMovieId());
        badgeService.checkAndAward(userId, "review_count", reviewMapper.selectCountByUser(userId));

        return review.getId();
    }

    @Override
    @Transactional
    public void updateReview(Long reviewId, ReviewRequest request) {
        Review review = reviewMapper.selectById(reviewId);
        if (review == null) {
            throw new BusinessException(404, "影评不存在");
        }
        if (!review.getUserId().equals(CurrentUser.getUserId())) {
            throw new BusinessException(403, "只能修改自己的影评");
        }

        review.setRating(request.getRating());
        review.setContent(request.getContent());
        reviewMapper.update(review);

        // 重新计算该影视的平均评分
        recalcMovieRating(review.getMovieId());
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId) {
        Review review = reviewMapper.selectById(reviewId);
        if (review == null) {
            throw new BusinessException(404, "影评不存在");
        }
        if (!review.getUserId().equals(CurrentUser.getUserId())) {
            throw new BusinessException(403, "只能删除自己的影评");
        }

        Long movieId = review.getMovieId();
        reviewMapper.deleteById(reviewId);

        // 重新计算该影视的平均评分
        recalcMovieRating(movieId);
    }

    @Override
    public void replyReview(Long reviewId, ReplyRequest request) {
        Review review = reviewMapper.selectById(reviewId);
        if (review == null) {
            throw new BusinessException(404, "影评不存在");
        }

        // 检查是否为该影视剧的发布者
        Long userId = CurrentUser.getUserId();
        var movieDetail = movieMapper.selectById(review.getMovieId());
        if (movieDetail == null || !movieDetail.getPublisherId().equals(userId)) {
            throw new BusinessException(403, "只有该影视剧的发布者才能回复影评");
        }

        reviewMapper.updateReply(reviewId, request.getReply());
    }

    /**
     * 重新计算影视剧的平均评分和评分人数
     */
    private void recalcMovieRating(Long movieId) {
        BigDecimal avg = reviewMapper.selectAvgRatingByMovie(movieId);
        int count = reviewMapper.selectCountByMovie(movieId);
        if (avg == null) avg = BigDecimal.ZERO;
        movieMapper.updateRating(movieId, avg.setScale(1, RoundingMode.HALF_UP), count);
    }

    @Override
    public PageResult<ReviewResponse> listMyReviews(int page, int size) {
        Long userId = CurrentUser.getUserId();
        List<ReviewMapper.ReviewWithMovie> all = reviewMapper.selectByUserId(userId);

        int offset = (page - 1) * size;
        int toIndex = Math.min(offset + size, all.size());
        List<ReviewMapper.ReviewWithMovie> pageList = (offset >= all.size()) ? List.of() : all.subList(offset, toIndex);

        List<ReviewResponse> list = pageList.stream().map(r -> {
            ReviewResponse resp = new ReviewResponse();
            resp.setId(r.getId());
            resp.setUserId(r.getUserId());
            resp.setMovieId(r.getMovieId());
            resp.setMovieTitle(r.getMovieTitle());
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
}
