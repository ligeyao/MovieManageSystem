package com.j2ee.MovieManageSystem.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Badge {
    private Long id;
    private String name;
    private String description;
    private String icon;
    private String ruleType;
    private Integer ruleValue;
    private LocalDateTime createdAt;

    // 非数据库字段：当前用户是否已获得
    private Boolean owned;
}
