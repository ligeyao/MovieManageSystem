package com.j2ee.MovieManageSystem.mapper;

import com.j2ee.MovieManageSystem.entity.UserMovieStatus;
import org.apache.ibatis.annotations.*;

/**
 * 用户影视状态 Mapper
 */
@Mapper
public interface UserMovieStatusMapper {

    @Select("SELECT * FROM user_movie_status WHERE user_id = #{userId} AND movie_id = #{movieId}")
    UserMovieStatus selectByUserAndMovie(@Param("userId") Long userId,
                                          @Param("movieId") Long movieId);

    @Insert("INSERT INTO user_movie_status (user_id, movie_id, status, watch_date) " +
            "VALUES (#{userId}, #{movieId}, #{status}, #{watchDate})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserMovieStatus status);

    @Update("UPDATE user_movie_status SET status = #{status}, watch_date = #{watchDate} WHERE id = #{id}")
    int update(UserMovieStatus status);

    @Select("SELECT COUNT(*) FROM user_movie_status WHERE movie_id = #{movieId} AND status = 'watched'")
    int countWatchedByMovie(Long movieId);

    @Select("SELECT DISTINCT movie_id FROM user_movie_status WHERE user_id = #{userId} AND status = 'watched'")
    java.util.List<Long> selectWatchedMovieIds(Long userId);

    @Select("SELECT DISTINCT movie_id FROM user_movie_status WHERE user_id = #{userId} AND status = 'want_to_watch'")
    java.util.List<Long> selectWantToWatchMovieIds(Long userId);

    /** 按用户+状态联表查影视列表 */
    @Select("SELECT m.*, u.username AS publisher_name FROM user_movie_status ums " +
            "JOIN movie m ON ums.movie_id = m.id " +
            "LEFT JOIN user u ON m.publisher_id = u.id " +
            "WHERE ums.user_id = #{userId} AND ums.status = #{status} " +
            "ORDER BY ums.created_at DESC")
    java.util.List<MovieMapper.MovieDetail> selectMoviesByUserIdAndStatus(
            @Param("userId") Long userId,
            @Param("status") String status);
}
