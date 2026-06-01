package com.j2ee.MovieManageSystem.service.impl;

import com.j2ee.MovieManageSystem.common.BusinessException;
import com.j2ee.MovieManageSystem.common.PageResult;
import com.j2ee.MovieManageSystem.dto.response.ReviewResponse;
import com.j2ee.MovieManageSystem.interceptor.CurrentUser;
import com.j2ee.MovieManageSystem.mapper.ReviewFavoriteMapper;
import com.j2ee.MovieManageSystem.service.ReviewFavoriteService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewFavoriteServiceImpl implements ReviewFavoriteService {

    private final ReviewFavoriteMapper mapper;

    public ReviewFavoriteServiceImpl(ReviewFavoriteMapper mapper) { this.mapper = mapper; }

    @Override
    public void addFavorite(Long reviewId) {
        try {
            mapper.insert(CurrentUser.getUserId(), reviewId);
        } catch (Exception e) {
            throw new BusinessException(400, "已收藏过该影评");
        }
    }

    @Override
    public void removeFavorite(Long reviewId) {
        mapper.delete(CurrentUser.getUserId(), reviewId);
    }

    @Override
    public List<Long> getMyFavoriteIds() {
        return mapper.selectReviewIdsByUser(CurrentUser.getUserId());
    }

    @Override
    public PageResult<ReviewResponse> listMyFavorites(int page, int size) {
        List<ReviewFavoriteMapper.FavReview> all = mapper.selectFavReviewsByUser(CurrentUser.getUserId());
        int offset = (page - 1) * size;
        int toIndex = Math.min(offset + size, all.size());
        List<ReviewFavoriteMapper.FavReview> pageList = (offset >= all.size()) ? List.of() : all.subList(offset, toIndex);

        List<ReviewResponse> list = pageList.stream().map(r -> {
            ReviewResponse resp = new ReviewResponse();
            resp.setId(r.getId());
            resp.setUserId(null);
            resp.setMovieId(r.getMovieId());
            resp.setMovieTitle(r.getMovieTitle());
            resp.setUsername(r.getUsername());
            resp.setRating(r.getRating());
            resp.setContent(r.getContent());
            resp.setCreatedAt(r.getCreatedAt());
            return resp;
        }).collect(Collectors.toList());

        return PageResult.of(all.size(), page, size, list);
    }
}
