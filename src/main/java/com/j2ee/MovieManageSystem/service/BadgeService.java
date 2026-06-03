package com.j2ee.MovieManageSystem.service;

import com.j2ee.MovieManageSystem.entity.Badge;
import java.util.List;

public interface BadgeService {

    // 管理员
    List<Badge> listAll();
    Badge createBadge(Badge badge);
    Badge updateBadge(Badge badge);
    void deleteBadge(Long id);

    // 用户端
    List<Badge> listMyBadges();

    // 自动发放（由其他Service触发）
    void checkAndAward(Long userId, String ruleType, int currentCount);
}
