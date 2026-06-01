package com.j2ee.MovieManageSystem.mapper;

import lombok.Data;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ReviewFavoriteMapper {

    @Insert("INSERT INTO review_favorite (user_id, review_id) VALUES (#{userId}, #{reviewId})")
    int insert(@Param("userId") Long userId, @Param("reviewId") Long reviewId);

    @Delete("DELETE FROM review_favorite WHERE user_id = #{userId} AND review_id = #{reviewId}")
    int delete(@Param("userId") Long userId, @Param("reviewId") Long reviewId);

    @Select("SELECT review_id FROM review_favorite WHERE user_id = #{userId}")
    List<Long> selectReviewIdsByUser(Long userId);

    @Select("SELECT COUNT(*) FROM review_favorite WHERE review_id = #{reviewId}")
    int countByReview(Long reviewId);

    /** 收藏的影评列表（联表查用户名+影评内容+影片名） */
    @Select("SELECT rf.review_id AS id, r.rating, r.content, r.created_at, " +
            "u.username, m.title_cn AS movie_title, r.movie_id " +
            "FROM review_favorite rf " +
            "JOIN review r ON rf.review_id = r.id " +
            "JOIN user u ON r.user_id = u.id " +
            "JOIN movie m ON r.movie_id = m.id " +
            "WHERE rf.user_id = #{userId} ORDER BY rf.created_at DESC")
    List<FavReview> selectFavReviewsByUser(Long userId);

    @Data
    class FavReview {
        private Long id;
        private Integer rating;
        private String content;
        private java.time.LocalDateTime createdAt;
        private String username;
        private String movieTitle;
        private Long movieId;
    }
}
