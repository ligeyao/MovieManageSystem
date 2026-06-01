package com.j2ee.MovieManageSystem.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 影评列表项响应
 */
@Data
public class ReviewResponse {
    private Long id;
    private Long userId;
    private String username;
    private Integer rating;
    private String content;
    private String reply;
    private LocalDateTime replyAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
