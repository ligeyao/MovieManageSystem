package com.j2ee.MovieManageSystem.controller;

import com.j2ee.MovieManageSystem.common.Result;
import com.j2ee.MovieManageSystem.entity.Badge;
import com.j2ee.MovieManageSystem.service.BadgeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BadgeController {

    private final BadgeService badgeService;

    public BadgeController(BadgeService badgeService) { this.badgeService = badgeService; }

    // --- 用户 ---
    @GetMapping("/badges/my")
    public Result<List<Badge>> myBadges() {
        return Result.ok(badgeService.listMyBadges());
    }

    // --- 管理员 ---
    @GetMapping("/admin/badges")
    public Result<List<Badge>> listAll() {
        return Result.ok(badgeService.listAll());
    }

    @PostMapping("/admin/badges")
    public Result<Badge> create(@RequestBody Badge badge) {
        return Result.ok("创建成功", badgeService.createBadge(badge));
    }

    @PutMapping("/admin/badges/{id}")
    public Result<Badge> update(@PathVariable Long id, @RequestBody Badge badge) {
        badge.setId(id);
        return Result.ok("更新成功", badgeService.updateBadge(badge));
    }

    @DeleteMapping("/admin/badges/{id}")
    public Result<?> delete(@PathVariable Long id) {
        badgeService.deleteBadge(id);
        return Result.ok("删除成功");
    }
}
