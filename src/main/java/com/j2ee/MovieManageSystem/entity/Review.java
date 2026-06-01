package com.j2ee.MovieManageSystem.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 影评实体
 */
@Data
public class Review {
    private Long id;
    private Long userId;
    private Long movieId;
    private Integer rating;     // 1-10
    private String content;
    private String reply;       // 发布者回复
    private LocalDateTime replyAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
