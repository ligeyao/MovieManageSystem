package com.j2ee.MovieManageSystem.service;

import com.j2ee.MovieManageSystem.common.PageResult;
import com.j2ee.MovieManageSystem.dto.request.MovieRequest;
import com.j2ee.MovieManageSystem.dto.response.MovieDetailResponse;
import com.j2ee.MovieManageSystem.dto.response.MovieListResponse;

public interface MovieService {

    PageResult<MovieListResponse> listMovies(int page, int size, String keyword,
                                              String genre, String language, String filterMode,
                                              String type, Integer year, String country,
                                              Long publisherId, String sort);

    MovieDetailResponse getMovieDetail(Long movieId);
    Long createMovie(MovieRequest request);
    void updateMovie(Long movieId, MovieRequest request);
    void deleteMovie(Long movieId);
    int batchImport(String text);
}
