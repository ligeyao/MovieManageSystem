package com.j2ee.MovieManageSystem.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 影视列表项响应
 */
@Data
public class MovieListResponse {
    private Long id;
    private String titleCn;
    private String titleEn;
    private String posterUrl;
    private String director;
    private String genre;
    private Integer year;
    private String country;
    private Integer duration;
    private BigDecimal avgRating;
    private Integer ratingCount;
    private Integer watchedCount;
    private Integer favoriteCount;
    private Long publisherId;
    private String publisherName;
    private LocalDateTime createdAt;
}
