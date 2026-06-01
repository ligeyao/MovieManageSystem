package com.j2ee.MovieManageSystem.mapper;

import com.j2ee.MovieManageSystem.entity.Movie;
import lombok.Data;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 影视剧 Mapper
 */
@Mapper
public interface MovieMapper {

    @Insert("INSERT INTO movie (title_cn, title_en, poster_url, director, actors, " +
            "genre, year, country, language, duration, description, platform, awards, publisher_id) " +
            "VALUES (#{titleCn}, #{titleEn}, #{posterUrl}, #{director}, #{actors}, " +
            "#{genre}, #{year}, #{country}, #{language}, #{duration}, #{description}, " +
            "#{platform}, #{awards}, #{publisherId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Movie movie);

    @Update("<script>" +
            "UPDATE movie SET " +
            "<if test='titleCn != null'>title_cn = #{titleCn}, </if>" +
            "<if test='titleEn != null'>title_en = #{titleEn}, </if>" +
            "<if test='posterUrl != null'>poster_url = #{posterUrl}, </if>" +
            "<if test='director != null'>director = #{director}, </if>" +
            "<if test='actors != null'>actors = #{actors}, </if>" +
            "<if test='genre != null'>genre = #{genre}, </if>" +
            "<if test='year != null'>year = #{year}, </if>" +
            "<if test='country != null'>country = #{country}, </if>" +
            "<if test='language != null'>language = #{language}, </if>" +
            "<if test='duration != null'>duration = #{duration}, </if>" +
            "<if test='description != null'>description = #{description}, </if>" +
            "<if test='platform != null'>platform = #{platform}, </if>" +
            "<if test='awards != null'>awards = #{awards}, </if>" +
            "id = #{id} WHERE id = #{id}</script>")
    int update(Movie movie);

    @Delete("DELETE FROM movie WHERE id = #{id}")
    int deleteById(Long id);

    @Select("SELECT m.*, u.username AS publisher_name " +
            "FROM movie m LEFT JOIN user u ON m.publisher_id = u.id " +
            "WHERE m.id = #{id}")
    MovieDetail selectById(Long id);

    @Select("<script>" +
            "SELECT m.*, u.username AS publisher_name FROM movie m " +
            "LEFT JOIN user u ON m.publisher_id = u.id WHERE 1=1 " +
            "<if test='keyword != null and keyword != \"\"'>AND m.title_cn LIKE CONCAT('%',#{keyword},'%') </if>" +
            "<if test='genre != null and genre != \"\"'>AND m.genre = #{genre} </if>" +
            "<if test='year != null'>AND m.year = #{year} </if>" +
            "<if test='country != null and country != \"\"'>AND m.country = #{country} </if>" +
            "<if test='publisherId != null'>AND m.publisher_id = #{publisherId} </if>" +
            "<if test='sort == \"rating\"'>ORDER BY m.avg_rating DESC</if>" +
            "<if test='sort == \"year\"'>ORDER BY m.year DESC</if>" +
            "<if test='sort == \"watched\"'>ORDER BY m.watched_count DESC</if>" +
            "<if test='sort == null or sort == \"\"'>ORDER BY m.created_at DESC</if>" +
            "</script>")
    List<MovieDetail> selectPage(@Param("keyword") String keyword,
                                  @Param("genre") String genre,
                                  @Param("year") Integer year,
                                  @Param("country") String country,
                                  @Param("publisherId") Long publisherId,
                                  @Param("sort") String sort);

    /**
     * MovieDetail 内部类（含 publisherName）
     */
    @Data
    class MovieDetail {
        private Long id;
        private String titleCn;
        private String titleEn;
        private String posterUrl;
        private String director;
        private String actors;
        private String genre;
        private Integer year;
        private String country;
        private String language;
        private Integer duration;
        private String description;
        private String platform;
        private String awards;
        private java.math.BigDecimal avgRating;
        private Integer ratingCount;
        private Integer watchedCount;
        private Integer favoriteCount;
        private Long publisherId;
        private String publisherName;
        private java.time.LocalDateTime createdAt;
        private java.time.LocalDateTime updatedAt;
    }

    // ---- 榜单查询 ----

    @Select("SELECT * FROM movie WHERE avg_rating >= 7.0 AND rating_count >= 3 " +
            "ORDER BY avg_rating DESC")
    List<Movie> selectHighRatingRanking();

    @Select("SELECT * FROM movie ORDER BY favorite_count DESC")
    List<Movie> selectFavoriteRanking();

    @Select("SELECT * FROM movie ORDER BY year DESC, created_at DESC")
    List<Movie> selectLatestRanking();

    @Select("SELECT * FROM movie ORDER BY watched_count DESC")
    List<Movie> selectMostWatchedRanking();

    // ---- 统计 ----

    @Select("SELECT DATE(created_at) AS label, COUNT(*) AS cnt " +
            "FROM movie " +
            "WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) " +
            "GROUP BY DATE(created_at) ORDER BY label")
    List<java.util.Map<String, Object>> countByDayLast30Days();

    // ---- 冗余字段更新 ----

    @Update("UPDATE movie SET avg_rating = #{avgRating}, rating_count = #{ratingCount} WHERE id = #{id}")
    int updateRating(@Param("id") Long id,
                     @Param("avgRating") java.math.BigDecimal avgRating,
                     @Param("ratingCount") int ratingCount);

    @Update("UPDATE movie SET watched_count = watched_count + #{delta} WHERE id = #{id}")
    int incrementWatchedCount(@Param("id") Long id, @Param("delta") int delta);

    @Update("UPDATE movie SET favorite_count = favorite_count + #{delta} WHERE id = #{id}")
    int incrementFavoriteCount(@Param("id") Long id, @Param("delta") int delta);
}
