package com.j2ee.MovieManageSystem.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String role;        // user / publisher / admin
    private String status;      // active / disabled
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
