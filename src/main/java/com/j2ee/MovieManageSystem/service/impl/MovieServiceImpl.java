package com.j2ee.MovieManageSystem.service.impl;

import com.j2ee.MovieManageSystem.common.BusinessException;
import com.j2ee.MovieManageSystem.common.PageResult;
import com.j2ee.MovieManageSystem.dto.request.MovieRequest;
import com.j2ee.MovieManageSystem.dto.response.MovieDetailResponse;
import com.j2ee.MovieManageSystem.dto.response.MovieListResponse;
import com.j2ee.MovieManageSystem.entity.Movie;
import com.j2ee.MovieManageSystem.interceptor.CurrentUser;
import com.j2ee.MovieManageSystem.mapper.*;
import com.j2ee.MovieManageSystem.service.MovieService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 影视剧服务实现
 */
@Service
public class MovieServiceImpl implements MovieService {

    private final MovieMapper movieMapper;
    private final UserMapper userMapper;
    private final FavoriteMapper favoriteMapper;
    private final UserMovieStatusMapper statusMapper;

    public MovieServiceImpl(MovieMapper movieMapper, UserMapper userMapper,
                            FavoriteMapper favoriteMapper, UserMovieStatusMapper statusMapper) {
        this.movieMapper = movieMapper;
        this.userMapper = userMapper;
        this.favoriteMapper = favoriteMapper;
        this.statusMapper = statusMapper;
    }

    @Override
    public PageResult<MovieListResponse> listMovies(int page, int size, String keyword,
                                                     String genre, String language, String filterMode,
                                                     Integer year, String country, Long publisherId,
                                                     String sort) {
        int offset = (page - 1) * size;

        List<MovieMapper.MovieDetail> details = movieMapper.selectPage(keyword, genre, language, filterMode, year, country, publisherId, sort);

        // 手动分页
        long total = details.size();
        int toIndex = Math.min(offset + size, details.size());
        List<MovieMapper.MovieDetail> pageList;
        if (offset >= details.size()) {
            pageList = List.of();
        } else {
            pageList = details.subList(offset, toIndex);
        }

        // 转换为响应对象
        List<MovieListResponse> list = pageList.stream().map(d -> {
            MovieListResponse resp = new MovieListResponse();
            resp.setId(d.getId());
            resp.setTitleCn(d.getTitleCn());
            resp.setTitleEn(d.getTitleEn());
            resp.setPosterUrl(d.getPosterUrl());
            resp.setDirector(d.getDirector());
            resp.setGenre(d.getGenre());
            resp.setYear(d.getYear());
            resp.setCountry(d.getCountry());
            resp.setDuration(d.getDuration());
            resp.setAvgRating(d.getAvgRating());
            resp.setRatingCount(d.getRatingCount());
            resp.setWatchedCount(d.getWatchedCount());
            resp.setFavoriteCount(d.getFavoriteCount());
            resp.setPublisherId(d.getPublisherId());
            resp.setPublisherName(d.getPublisherName());
            resp.setCreatedAt(d.getCreatedAt());
            return resp;
        }).collect(Collectors.toList());

        return PageResult.of(total, page, size, list);
    }

    @Override
    public MovieDetailResponse getMovieDetail(Long movieId) {
        MovieMapper.MovieDetail detail = movieMapper.selectById(movieId);
        if (detail == null) {
            throw new BusinessException(404, "影视剧不存在");
        }

        MovieDetailResponse resp = new MovieDetailResponse();
        resp.setId(detail.getId());
        resp.setTitleCn(detail.getTitleCn());
        resp.setTitleEn(detail.getTitleEn());
        resp.setPosterUrl(detail.getPosterUrl());
        resp.setDirector(detail.getDirector());
        resp.setActors(detail.getActors());
        resp.setGenre(detail.getGenre());
        resp.setYear(detail.getYear());
        resp.setCountry(detail.getCountry());
        resp.setLanguage(detail.getLanguage());
        resp.setDuration(detail.getDuration());
        resp.setDescription(detail.getDescription());
        resp.setPlatform(detail.getPlatform());
        resp.setAwards(detail.getAwards());
        resp.setAvgRating(detail.getAvgRating());
        resp.setRatingCount(detail.getRatingCount());
        resp.setWatchedCount(detail.getWatchedCount());
        resp.setFavoriteCount(detail.getFavoriteCount());
        resp.setPublisherId(detail.getPublisherId());
        resp.setPublisherName(detail.getPublisherName());
        resp.setCreatedAt(detail.getCreatedAt());

        // 获取当前用户对该片的状态
        try {
            Long userId = CurrentUser.getUserId();
            var status = statusMapper.selectByUserAndMovie(userId, movieId);
            if (status != null) {
                resp.setMyStatus(status.getStatus());
            }
            var fav = favoriteMapper.selectByUserAndMovie(userId, movieId);
            resp.setMyFavorite(fav != null);
        } catch (Exception ignored) {
            // 未登录情况（虽然接口需要登录，但做防御性处理）
        }

        return resp;
    }

    @Override
    public Long createMovie(MovieRequest request) {
        String role = CurrentUser.getRole();
        if (!"publisher".equals(role)) {
            throw new BusinessException(403, "仅发布者可以添加影视剧");
        }

        Movie movie = new Movie();
        movie.setTitleCn(request.getTitleCn());
        movie.setTitleEn(request.getTitleEn());
        movie.setPosterUrl(request.getPosterUrl());
        movie.setDirector(request.getDirector());
        movie.setActors(request.getActors());
        movie.setGenre(request.getGenre());
        movie.setYear(request.getYear());
        movie.setCountry(request.getCountry());
        movie.setLanguage(request.getLanguage());
        movie.setDuration(request.getDuration());
        movie.setDescription(request.getDescription());
        movie.setPlatform(request.getPlatform());
        movie.setAwards(request.getAwards());
        movie.setPublisherId(CurrentUser.getUserId());

        movieMapper.insert(movie);
        return movie.getId();
    }

    @Override
    public void updateMovie(Long movieId, MovieRequest request) {
        MovieMapper.MovieDetail detail = movieMapper.selectById(movieId);
        if (detail == null) {
            throw new BusinessException(404, "影视剧不存在");
        }

        // 仅发布者本人可以编辑
        Long userId = CurrentUser.getUserId();
        if (!detail.getPublisherId().equals(userId)) {
            throw new BusinessException(403, "只能编辑自己发布的影视剧");
        }

        Movie movie = new Movie();
        movie.setId(movieId);
        movie.setTitleCn(request.getTitleCn());
        movie.setTitleEn(request.getTitleEn());
        movie.setPosterUrl(request.getPosterUrl());
        movie.setDirector(request.getDirector());
        movie.setActors(request.getActors());
        movie.setGenre(request.getGenre());
        movie.setYear(request.getYear());
        movie.setCountry(request.getCountry());
        movie.setLanguage(request.getLanguage());
        movie.setDuration(request.getDuration());
        movie.setDescription(request.getDescription());
        movie.setPlatform(request.getPlatform());
        movie.setAwards(request.getAwards());

        movieMapper.update(movie);
    }

    @Override
    public void deleteMovie(Long movieId) {
        MovieMapper.MovieDetail detail = movieMapper.selectById(movieId);
        if (detail == null) {
            throw new BusinessException(404, "影视剧不存在");
        }

        // 仅发布者本人或管理员可以删除
        Long userId = CurrentUser.getUserId();
        String role = CurrentUser.getRole();
        if (!detail.getPublisherId().equals(userId) && !"admin".equals(role)) {
            throw new BusinessException(403, "无权删除此影视剧");
        }

        movieMapper.deleteById(movieId);
    }
}
