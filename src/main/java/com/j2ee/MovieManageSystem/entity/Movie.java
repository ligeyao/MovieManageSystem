package com.j2ee.MovieManageSystem.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 影视剧实体
 */
@Data
public class Movie {
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
    private String type;        // movie / tv
    private BigDecimal avgRating;
    private Integer ratingCount;
    private Integer watchedCount;
    private Integer favoriteCount;
    private Long publisherId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
