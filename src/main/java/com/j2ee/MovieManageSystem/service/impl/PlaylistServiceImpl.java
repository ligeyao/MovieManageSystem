package com.j2ee.MovieManageSystem.service.impl;

import com.j2ee.MovieManageSystem.common.BusinessException;
import com.j2ee.MovieManageSystem.common.PageResult;
import com.j2ee.MovieManageSystem.dto.response.MovieListResponse;
import com.j2ee.MovieManageSystem.entity.Playlist;
import com.j2ee.MovieManageSystem.interceptor.CurrentUser;
import com.j2ee.MovieManageSystem.mapper.MovieMapper;
import com.j2ee.MovieManageSystem.mapper.PlaylistMapper;
import com.j2ee.MovieManageSystem.service.PlaylistService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlaylistServiceImpl implements PlaylistService {

    private final PlaylistMapper playlistMapper;
    private final MovieMapper movieMapper;

    public PlaylistServiceImpl(PlaylistMapper playlistMapper, MovieMapper movieMapper) {
        this.playlistMapper = playlistMapper;
        this.movieMapper = movieMapper;
    }

    @Override
    public List<Playlist> listMyPlaylists() {
        return playlistMapper.selectByUserId(CurrentUser.getUserId());
    }

    @Override
    public Playlist createPlaylist(String name, String description) {
        Playlist p = new Playlist();
        p.setUserId(CurrentUser.getUserId());
        p.setName(name);
        p.setDescription(description);
        playlistMapper.insert(p);
        return p;
    }

    @Override
    public void updatePlaylist(Long id, String name, String description) {
        Playlist p = new Playlist();
        p.setId(id);
        p.setUserId(CurrentUser.getUserId());
        p.setName(name);
        p.setDescription(description);
        playlistMapper.update(p);
    }

    @Override
    @Transactional
    public void deletePlaylist(Long id) {
        playlistMapper.deleteById(id, CurrentUser.getUserId());
    }

    @Override
    public void addMovieToPlaylist(Long playlistId, Long movieId) {
        Playlist p = playlistMapper.selectById(playlistId);
        if (p == null || !p.getUserId().equals(CurrentUser.getUserId())) {
            throw new BusinessException(403, "无权操作此片单");
        }
        try {
            playlistMapper.addMovie(playlistId, movieId);
        } catch (Exception e) {
            throw new BusinessException(400, "该影视已在片单中");
        }
    }

    @Override
    public void removeMovieFromPlaylist(Long playlistId, Long movieId) {
        playlistMapper.removeMovie(playlistId, movieId);
    }

    @Override
    public PageResult<MovieListResponse> listMoviesInPlaylist(Long playlistId, int page, int size) {
        List<MovieMapper.MovieDetail> all = playlistMapper.selectMoviesByPlaylist(playlistId);
        int offset = (page - 1) * size;
        int toIndex = Math.min(offset + size, all.size());
        List<MovieMapper.MovieDetail> pageList = (offset >= all.size()) ? List.of() : all.subList(offset, toIndex);

        List<MovieListResponse> list = pageList.stream().map(d -> {
            MovieListResponse r = new MovieListResponse();
            r.setId(d.getId()); r.setTitleCn(d.getTitleCn()); r.setTitleEn(d.getTitleEn());
            r.setPosterUrl(d.getPosterUrl()); r.setDirector(d.getDirector());
            r.setGenre(d.getGenre()); r.setYear(d.getYear()); r.setCountry(d.getCountry());
            r.setDuration(d.getDuration()); r.setAvgRating(d.getAvgRating());
            r.setRatingCount(d.getRatingCount()); r.setWatchedCount(d.getWatchedCount());
            r.setFavoriteCount(d.getFavoriteCount());
            r.setPublisherId(d.getPublisherId()); r.setPublisherName(d.getPublisherName());
            r.setCreatedAt(d.getCreatedAt());
            return r;
        }).collect(Collectors.toList());

        return PageResult.of(all.size(), page, size, list);
    }

    @Override
    public List<Long> getMyPlaylistIdsByMovie(Long movieId) {
        return playlistMapper.selectPlaylistIdsByMovie(movieId);
    }
}
