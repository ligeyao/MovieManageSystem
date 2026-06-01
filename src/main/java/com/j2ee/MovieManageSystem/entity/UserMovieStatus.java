package com.j2ee.MovieManageSystem.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户-影视状态实体（想看/看过）
 */
@Data
public class UserMovieStatus {
    private Long id;
    private Long userId;
    private Long movieId;
    private String status;      // want_to_watch / watched
    private LocalDateTime watchDate;
    private LocalDateTime createdAt;
}
