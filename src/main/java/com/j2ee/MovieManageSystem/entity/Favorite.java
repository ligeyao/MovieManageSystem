package com.j2ee.MovieManageSystem.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 收藏实体
 */
@Data
public class Favorite {
    private Long id;
    private Long userId;
    private Long movieId;
    private LocalDateTime createdAt;
}
