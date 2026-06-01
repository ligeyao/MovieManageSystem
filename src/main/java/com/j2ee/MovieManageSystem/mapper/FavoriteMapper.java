package com.j2ee.MovieManageSystem.mapper;

import com.j2ee.MovieManageSystem.entity.Favorite;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 收藏 Mapper
 */
@Mapper
public interface FavoriteMapper {

    @Select("SELECT * FROM favorite WHERE user_id = #{userId} AND movie_id = #{movieId}")
    Favorite selectByUserAndMovie(@Param("userId") Long userId,
                                   @Param("movieId") Long movieId);

    @Select("SELECT * FROM favorite WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<Favorite> selectByUserId(Long userId);

    @Insert("INSERT INTO favorite (user_id, movie_id) VALUES (#{userId}, #{movieId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Favorite favorite);

    @Delete("DELETE FROM favorite WHERE user_id = #{userId} AND movie_id = #{movieId}")
    int deleteByUserAndMovie(@Param("userId") Long userId,
                              @Param("movieId") Long movieId);

    @Select("SELECT movie_id FROM favorite WHERE user_id = #{userId}")
    List<Long> selectFavoriteMovieIds(Long userId);

    @Select("SELECT COUNT(*) FROM favorite WHERE movie_id = #{movieId}")
    int countByMovie(Long movieId);
}
