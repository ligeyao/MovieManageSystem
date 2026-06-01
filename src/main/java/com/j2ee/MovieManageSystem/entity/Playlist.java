package com.j2ee.MovieManageSystem.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Playlist {
    private Long id;
    private Long userId;
    private String name;
    private String description;
    private LocalDateTime createdAt;

    // 非数据库字段，用于列表展示
    private Integer movieCount;
}
