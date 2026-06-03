package com.j2ee.MovieManageSystem.service.impl;

import com.j2ee.MovieManageSystem.entity.Badge;
import com.j2ee.MovieManageSystem.mapper.BadgeMapper;
import com.j2ee.MovieManageSystem.service.BadgeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BadgeServiceImpl implements BadgeService {

    private final BadgeMapper badgeMapper;

    public BadgeServiceImpl(BadgeMapper badgeMapper) { this.badgeMapper = badgeMapper; }

    @Override
    public List<Badge> listAll() { return badgeMapper.selectAll(); }

    @Override
    public Badge createBadge(Badge badge) {
        badgeMapper.insert(badge);
        return badge;
    }

    @Override
    public Badge updateBadge(Badge badge) {
        badgeMapper.update(badge);
        return badge;
    }

    @Override
    public void deleteBadge(Long id) { badgeMapper.delete(id); }

    @Override
    public List<Badge> listMyBadges() { return badgeMapper.selectBadgesByUser(com.j2ee.MovieManageSystem.interceptor.CurrentUser.getUserId()); }

    @Override
    public void checkAndAward(Long userId, String ruleType, int currentCount) {
        List<Badge> badges = badgeMapper.selectAll();
        List<Long> owned = badgeMapper.selectBadgeIdsByUser(userId);
        for (Badge b : badges) {
            if (b.getRuleType().equals(ruleType) && currentCount >= b.getRuleValue() && !owned.contains(b.getId())) {
                badgeMapper.awardBadge(userId, b.getId());
            }
        }
    }
}
