package com.j2ee.MovieManageSystem.service.impl;

import com.j2ee.MovieManageSystem.common.BusinessException;
import com.j2ee.MovieManageSystem.common.PageResult;
import com.j2ee.MovieManageSystem.dto.response.MovieListResponse;
import com.j2ee.MovieManageSystem.entity.Favorite;
import com.j2ee.MovieManageSystem.entity.Movie;
import com.j2ee.MovieManageSystem.interceptor.CurrentUser;
import com.j2ee.MovieManageSystem.mapper.FavoriteMapper;
import com.j2ee.MovieManageSystem.mapper.MovieMapper;
import com.j2ee.MovieManageSystem.service.FavoriteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 收藏服务实现
 */
@Service
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteMapper favoriteMapper;
    private final MovieMapper movieMapper;

    public FavoriteServiceImpl(FavoriteMapper favoriteMapper, MovieMapper movieMapper) {
        this.favoriteMapper = favoriteMapper;
        this.movieMapper = movieMapper;
    }

    @Override
    @Transactional
    public void addFavorite(Long movieId) {
        Long userId = CurrentUser.getUserId();

        // 检查是否已收藏
        Favorite existing = favoriteMapper.selectByUserAndMovie(userId, movieId);
        if (existing != null) {
            throw new BusinessException(400, "已收藏过该影视剧");
        }

        // 检查影视是否存在
        if (movieMapper.selectById(movieId) == null) {
            throw new BusinessException(404, "影视剧不存在");
        }

        Favorite fav = new Favorite();
        fav.setUserId(userId);
        fav.setMovieId(movieId);
        favoriteMapper.insert(fav);

        // 更新影视收藏数 +1
        movieMapper.incrementFavoriteCount(movieId, 1);
    }

    @Override
    @Transactional
    public void removeFavorite(Long movieId) {
        Long userId = CurrentUser.getUserId();

        int deleted = favoriteMapper.deleteByUserAndMovie(userId, movieId);
        if (deleted == 0) {
            throw new BusinessException(400, "未收藏该影视剧");
        }

        // 更新影视收藏数 -1
        movieMapper.incrementFavoriteCount(movieId, -1);
    }

    @Override
    public PageResult<MovieListResponse> listMyFavorites(int page, int size) {
        Long userId = CurrentUser.getUserId();
        List<Favorite> favorites = favoriteMapper.selectByUserId(userId);

        int offset = (page - 1) * size;
        int toIndex = Math.min(offset + size, favorites.size());
        List<Favorite> pageList;
        if (offset >= favorites.size()) {
            pageList = List.of();
        } else {
            pageList = favorites.subList(offset, toIndex);
        }

        // 根据收藏的 movieId 查询影视信息
        List<MovieListResponse> list = pageList.stream().map(fav -> {
            var detail = movieMapper.selectById(fav.getMovieId());
            if (detail == null) return null;
            MovieListResponse resp = new MovieListResponse();
            resp.setId(detail.getId());
            resp.setTitleCn(detail.getTitleCn());
            resp.setTitleEn(detail.getTitleEn());
            resp.setPosterUrl(detail.getPosterUrl());
            resp.setDirector(detail.getDirector());
            resp.setGenre(detail.getGenre());
            resp.setYear(detail.getYear());
            resp.setCountry(detail.getCountry());
            resp.setDuration(detail.getDuration());
            resp.setAvgRating(detail.getAvgRating());
            resp.setRatingCount(detail.getRatingCount());
            resp.setWatchedCount(detail.getWatchedCount());
            resp.setFavoriteCount(detail.getFavoriteCount());
            resp.setCreatedAt(detail.getCreatedAt());
            return resp;
        }).filter(r -> r != null).collect(Collectors.toList());

        return PageResult.of(favorites.size(), page, size, list);
    }
}
