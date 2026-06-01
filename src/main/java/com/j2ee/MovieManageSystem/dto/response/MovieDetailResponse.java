package com.j2ee.MovieManageSystem.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 影视详情响应
 */
@Data
public class MovieDetailResponse {
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
    private BigDecimal avgRating;
    private Integer ratingCount;
    private Integer watchedCount;
    private Integer favoriteCount;
    private Long publisherId;
    private String publisherName;
    private String myStatus;        // null / want_to_watch / watched
    private Boolean myFavorite;
    private LocalDateTime createdAt;
}
