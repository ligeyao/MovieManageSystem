package com.j2ee.MovieManageSystem.mapper;

import com.j2ee.MovieManageSystem.entity.Playlist;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PlaylistMapper {

    @Select("SELECT p.*, (SELECT COUNT(*) FROM playlist_movie pm WHERE pm.playlist_id = p.id) AS movie_count " +
            "FROM playlist p WHERE p.user_id = #{userId} ORDER BY p.created_at DESC")
    List<Playlist> selectByUserId(Long userId);

    @Select("SELECT * FROM playlist WHERE id = #{id}")
    Playlist selectById(Long id);

    @Insert("INSERT INTO playlist (user_id, name, description) VALUES (#{userId}, #{name}, #{description})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Playlist playlist);

    @Update("UPDATE playlist SET name = #{name}, description = #{description} WHERE id = #{id} AND user_id = #{userId}")
    int update(Playlist playlist);

    @Delete("DELETE FROM playlist WHERE id = #{id} AND user_id = #{userId}")
    int deleteById(@Param("id") Long id, @Param("userId") Long userId);

    // ---- 片单内影视 ----

    @Insert("INSERT INTO playlist_movie (playlist_id, movie_id) VALUES (#{playlistId}, #{movieId})")
    int addMovie(@Param("playlistId") Long playlistId, @Param("movieId") Long movieId);

    @Delete("DELETE FROM playlist_movie WHERE playlist_id = #{playlistId} AND movie_id = #{movieId}")
    int removeMovie(@Param("playlistId") Long playlistId, @Param("movieId") Long movieId);

    @Select("SELECT movie_id FROM playlist_movie WHERE playlist_id = #{playlistId}")
    List<Long> selectMovieIdsByPlaylist(Long playlistId);

    @Select("SELECT m.*, u.username AS publisher_name FROM playlist_movie pm " +
            "JOIN movie m ON pm.movie_id = m.id " +
            "LEFT JOIN user u ON m.publisher_id = u.id " +
            "WHERE pm.playlist_id = #{playlistId} ORDER BY pm.added_at DESC")
    List<MovieMapper.MovieDetail> selectMoviesByPlaylist(Long playlistId);

    @Select("SELECT playlist_id FROM playlist_movie WHERE movie_id = #{movieId}")
    List<Long> selectPlaylistIdsByMovie(Long movieId);
}
