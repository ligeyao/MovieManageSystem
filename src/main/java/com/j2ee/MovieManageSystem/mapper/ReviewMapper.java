package com.j2ee.MovieManageSystem.mapper;

import com.j2ee.MovieManageSystem.entity.Review;
import lombok.Data;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 影评 Mapper
 */
@Mapper
public interface ReviewMapper {

    @Select("SELECT r.*, u.username FROM review r " +
            "LEFT JOIN user u ON r.user_id = u.id " +
            "WHERE r.movie_id = #{movieId} " +
            "ORDER BY r.created_at DESC")
    List<ReviewWithUser> selectByMovieId(Long movieId);

    @Select("SELECT * FROM review WHERE user_id = #{userId} AND movie_id = #{movieId}")
    Review selectByUserAndMovie(@Param("userId") Long userId,
                                 @Param("movieId") Long movieId);

    @Select("SELECT * FROM review WHERE id = #{id}")
    Review selectById(Long id);

    @Insert("INSERT INTO review (user_id, movie_id, rating, content) " +
            "VALUES (#{userId}, #{movieId}, #{rating}, #{content})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Review review);

    @Update("UPDATE review SET rating = #{rating}, content = #{content} WHERE id = #{id}")
    int update(Review review);

    @Update("UPDATE review SET reply = #{reply}, reply_at = NOW() WHERE id = #{id}")
    int updateReply(@Param("id") Long id, @Param("reply") String reply);

    @Delete("DELETE FROM review WHERE id = #{id}")
    int deleteById(Long id);

    @Select("SELECT AVG(rating) FROM review WHERE movie_id = #{movieId}")
    java.math.BigDecimal selectAvgRatingByMovie(Long movieId);

    @Select("SELECT COUNT(*) FROM review WHERE movie_id = #{movieId}")
    int selectCountByMovie(Long movieId);

    // ---- 管理员 ----

    @Select("<script>" +
            "SELECT r.*, u.username FROM review r " +
            "LEFT JOIN user u ON r.user_id = u.id WHERE 1=1 " +
            "<if test='keyword != null and keyword != \"\"'>AND r.content LIKE CONCAT('%',#{keyword},'%') </if>" +
            "ORDER BY r.created_at DESC</script>")
    List<ReviewWithUser> selectAllPage(@Param("keyword") String keyword);

    /**
     * 影评+用户名（用于列表展示）
     */
    @Data
    class ReviewWithUser {
        private Long id;
        private Long userId;
        private Long movieId;
        private Integer rating;
        private String content;
        private String reply;
        private java.time.LocalDateTime replyAt;
        private java.time.LocalDateTime createdAt;
        private java.time.LocalDateTime updatedAt;
        private String username;
    }
}
