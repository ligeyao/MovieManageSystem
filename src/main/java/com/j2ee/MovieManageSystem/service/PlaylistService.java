package com.j2ee.MovieManageSystem.service;

import com.j2ee.MovieManageSystem.common.PageResult;
import com.j2ee.MovieManageSystem.dto.response.MovieListResponse;
import com.j2ee.MovieManageSystem.entity.Playlist;

import java.util.List;

public interface PlaylistService {

    List<Playlist> listMyPlaylists();

    Playlist createPlaylist(String name, String description);

    void updatePlaylist(Long id, String name, String description);

    void deletePlaylist(Long id);

    void addMovieToPlaylist(Long playlistId, Long movieId);

    void removeMovieFromPlaylist(Long playlistId, Long movieId);

    PageResult<MovieListResponse> listMoviesInPlaylist(Long playlistId, int page, int size);

    /** 获取某影视所在的当前用户的片单ID列表（用于判断已加入哪些片单） */
    List<Long> getMyPlaylistIdsByMovie(Long movieId);
}
