package com.j2ee.MovieManageSystem.service.impl;

import com.j2ee.MovieManageSystem.common.PageResult;
import com.j2ee.MovieManageSystem.dto.response.MovieListResponse;
import com.j2ee.MovieManageSystem.entity.Movie;
import com.j2ee.MovieManageSystem.mapper.MovieMapper;
import com.j2ee.MovieManageSystem.service.RankingService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 榜单服务实现
 */
@Service
public class RankingServiceImpl implements RankingService {

    private final MovieMapper movieMapper;

    public RankingServiceImpl(MovieMapper movieMapper) {
        this.movieMapper = movieMapper;
    }

    @Override
    public PageResult<MovieListResponse> highRating(int page, int size) {
        return buildRanking(movieMapper::selectHighRatingRanking, page, size);
    }

    @Override
    public PageResult<MovieListResponse> favorite(int page, int size) {
        return buildRanking(movieMapper::selectFavoriteRanking, page, size);
    }

    @Override
    public PageResult<MovieListResponse> latest(int page, int size) {
        return buildRanking(movieMapper::selectLatestRanking, page, size);
    }

    @Override
    public PageResult<MovieListResponse> mostWatched(int page, int size) {
        return buildRanking(movieMapper::selectMostWatchedRanking, page, size);
    }

    /**
     * 通用榜单构建方法：手动分页 + Movie→MovieListResponse 转换
     */
    private PageResult<MovieListResponse> buildRanking(Supplier<List<Movie>> query, int page, int size) {
        List<Movie> all = query.get();
        int offset = (page - 1) * size;
        int toIndex = Math.min(offset + size, all.size());
        List<Movie> pageList = (offset >= all.size()) ? List.of() : all.subList(offset, toIndex);

        List<MovieListResponse> list = pageList.stream().map(m -> {
            MovieListResponse resp = new MovieListResponse();
            resp.setId(m.getId());
            resp.setTitleCn(m.getTitleCn());
            resp.setTitleEn(m.getTitleEn());
            resp.setPosterUrl(m.getPosterUrl());
            resp.setDirector(m.getDirector());
            resp.setGenre(m.getGenre());
            resp.setYear(m.getYear());
            resp.setCountry(m.getCountry());
            resp.setDuration(m.getDuration());
            resp.setAvgRating(m.getAvgRating());
            resp.setRatingCount(m.getRatingCount());
            resp.setWatchedCount(m.getWatchedCount());
            resp.setFavoriteCount(m.getFavoriteCount());
            resp.setCreatedAt(m.getCreatedAt());
            return resp;
        }).collect(Collectors.toList());

        return PageResult.of(all.size(), page, size, list);
    }
}
