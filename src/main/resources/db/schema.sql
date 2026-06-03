-- ============================================================
-- 影视剧管理与推荐系统 - 数据库建表脚本
-- Database: MySQL 8.0+
-- ============================================================

CREATE DATABASE IF NOT EXISTS movie_manage_system
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE movie_manage_system;

-- ============================================================
-- 1. 用户表 (user)
-- ============================================================
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '用户ID',
    `username`      VARCHAR(50)     NOT NULL                 COMMENT '用户名，登录用',
    `password`      VARCHAR(255)    NOT NULL                 COMMENT '密码（BCrypt加密）',
    `role`          VARCHAR(20)     NOT NULL DEFAULT 'user'  COMMENT '角色: user(普通用户) / publisher(发布者) / admin(管理员)',
    `status`        VARCHAR(20)     NOT NULL DEFAULT 'active' COMMENT '状态: active(正常) / disabled(禁用)',
    `created_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    `updated_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_role` (`role`),
    KEY `idx_status` (`status`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 初始化管理员账号（用户名: admin, 密码: admin123）
-- BCrypt 加密后的密码，实际使用时由后端生成
-- INSERT INTO `user` (`username`, `password`, `role`) VALUES ('admin', '$2a$10$...', 'admin');


-- ============================================================
-- 2. 影视剧表 (movie)
-- ============================================================
DROP TABLE IF EXISTS `movie`;
CREATE TABLE `movie` (
    `id`             BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '影视剧ID',
    `title_cn`       VARCHAR(200)    NOT NULL                 COMMENT '中文片名',
    `title_en`       VARCHAR(200)    DEFAULT NULL             COMMENT '外文片名（可选）',
    `poster_url`     VARCHAR(500)    DEFAULT NULL             COMMENT '海报图片URL',
    `director`       VARCHAR(100)    NOT NULL                 COMMENT '导演',
    `actors`         TEXT            DEFAULT NULL             COMMENT '演员列表（JSON数组或逗号分隔）',
    `genre`          VARCHAR(100)    NOT NULL                 COMMENT '类型，如：喜剧/动作/剧情',
    `year`           INT             NOT NULL                 COMMENT '上映年代',
    `country`        VARCHAR(100)    NOT NULL                 COMMENT '制片国家/地区',
    `language`       VARCHAR(50)     NOT NULL                 COMMENT '语言',
    `duration`       INT            NOT NULL                 COMMENT '片长（分钟）',
    `description`    TEXT            DEFAULT NULL             COMMENT '简介',
    `platform`       VARCHAR(100)    DEFAULT NULL             COMMENT '观看平台: Netflix / 爱奇艺 / 腾讯',
    `awards`         TEXT            DEFAULT NULL             COMMENT '获奖情况',
    `avg_rating`     DECIMAL(3,1)    NOT NULL DEFAULT 0.0     COMMENT '平均评分（系统自动计算）',
    `rating_count`   INT             NOT NULL DEFAULT 0       COMMENT '评分人数',
    `watched_count`  INT             NOT NULL DEFAULT 0       COMMENT '标记"看过"的用户数（冗余字段）',
    `favorite_count` INT             NOT NULL DEFAULT 0       COMMENT '收藏数（冗余字段）',
    `publisher_id`   BIGINT          NOT NULL                 COMMENT '发布者ID（关联user表）',
    `created_at`     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_title_cn` (`title_cn`),
    KEY `idx_genre` (`genre`),
    KEY `idx_year` (`year`),
    KEY `idx_country` (`country`),
    KEY `idx_avg_rating` (`avg_rating`),
    KEY `idx_rating_count` (`rating_count`),
    KEY `idx_watched_count` (`watched_count`),
    KEY `idx_favorite_count` (`favorite_count`),
    KEY `idx_publisher_id` (`publisher_id`),
    KEY `idx_created_at` (`created_at`),
    CONSTRAINT `fk_movie_publisher` FOREIGN KEY (`publisher_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='影视剧表';


-- ============================================================
-- 3. 用户-影视状态表 (user_movie_status)
--     记录"想看"和"看过"状态
-- ============================================================
DROP TABLE IF EXISTS `user_movie_status`;
CREATE TABLE `user_movie_status` (
    `id`          BIGINT      NOT NULL AUTO_INCREMENT  COMMENT '记录ID',
    `user_id`     BIGINT      NOT NULL                 COMMENT '用户ID',
    `movie_id`    BIGINT      NOT NULL                 COMMENT '影视剧ID',
    `status`      VARCHAR(20) NOT NULL                 COMMENT '状态: want_to_watch(想看) / watched(看过)',
    `watch_date`  DATETIME    DEFAULT NULL             COMMENT '标记"看过"的时间',
    `created_at`  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_movie` (`user_id`, `movie_id`),
    KEY `idx_movie_id` (`movie_id`),
    KEY `idx_status` (`status`),
    CONSTRAINT `fk_status_user`  FOREIGN KEY (`user_id`)  REFERENCES `user` (`id`)  ON DELETE CASCADE,
    CONSTRAINT `fk_status_movie` FOREIGN KEY (`movie_id`) REFERENCES `movie` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户影视状态表（想看/看过）';


-- ============================================================
-- 4. 影评表 (review)
--     用户写影评+打分；发布者可回复
-- ============================================================
DROP TABLE IF EXISTS `review`;
CREATE TABLE `review` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '影评ID',
    `user_id`     BIGINT       NOT NULL                 COMMENT '写影评的用户ID',
    `movie_id`    BIGINT       NOT NULL                 COMMENT '影视剧ID',
    `rating`      INT          NOT NULL                 COMMENT '评分 1-10',
    `content`     TEXT         DEFAULT NULL             COMMENT '影评内容',
    `reply`       TEXT         DEFAULT NULL             COMMENT '发布者回复内容',
    `reply_at`    DATETIME     DEFAULT NULL             COMMENT '回复时间',
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_movie_review` (`user_id`, `movie_id`),
    KEY `idx_movie_id` (`movie_id`),
    KEY `idx_rating` (`rating`),
    KEY `idx_created_at` (`created_at`),
    CONSTRAINT `fk_review_user`  FOREIGN KEY (`user_id`)  REFERENCES `user` (`id`)  ON DELETE CASCADE,
    CONSTRAINT `fk_review_movie` FOREIGN KEY (`movie_id`) REFERENCES `movie` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='影评表';


-- ============================================================
-- 5. 收藏表 (favorite)
-- ============================================================
DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite` (
    `id`         BIGINT   NOT NULL AUTO_INCREMENT  COMMENT '收藏ID',
    `user_id`    BIGINT   NOT NULL                 COMMENT '用户ID',
    `movie_id`   BIGINT   NOT NULL                 COMMENT '影视剧ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_movie_fav` (`user_id`, `movie_id`),
    KEY `idx_movie_id` (`movie_id`),
    CONSTRAINT `fk_fav_user`  FOREIGN KEY (`user_id`)  REFERENCES `user` (`id`)  ON DELETE CASCADE,
    CONSTRAINT `fk_fav_movie` FOREIGN KEY (`movie_id`) REFERENCES `movie` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收藏表';


-- ============================================================
-- 6. 片单表 (playlist)
-- ============================================================
DROP TABLE IF EXISTS `playlist`;
CREATE TABLE `playlist` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '片单ID',
    `user_id`     BIGINT       NOT NULL                COMMENT '用户ID',
    `name`        VARCHAR(100) NOT NULL                COMMENT '片单名称',
    `description` VARCHAR(500) DEFAULT NULL            COMMENT '片单描述',
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    CONSTRAINT `fk_playlist_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='片单表';


-- ============================================================
-- 7. 片单-影视关联表 (playlist_movie)
-- ============================================================
DROP TABLE IF EXISTS `playlist_movie`;
CREATE TABLE `playlist_movie` (
    `id`          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '关联ID',
    `playlist_id` BIGINT   NOT NULL                COMMENT '片单ID',
    `movie_id`    BIGINT   NOT NULL                COMMENT '影视剧ID',
    `added_at`    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_playlist_movie` (`playlist_id`, `movie_id`),
    KEY `idx_movie_id` (`movie_id`),
    CONSTRAINT `fk_pm_playlist` FOREIGN KEY (`playlist_id`) REFERENCES `playlist` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_pm_movie`    FOREIGN KEY (`movie_id`)    REFERENCES `movie` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='片单影视关联表';


-- ============================================================
-- 8. 影评收藏表 (review_favorite)
-- ============================================================
DROP TABLE IF EXISTS `review_favorite`;
CREATE TABLE `review_favorite` (
    `id`        BIGINT   NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `user_id`   BIGINT   NOT NULL                COMMENT '收藏者ID',
    `review_id` BIGINT   NOT NULL                COMMENT '影评ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_review` (`user_id`, `review_id`),
    KEY `idx_review_id` (`review_id`),
    CONSTRAINT `fk_rf_user`   FOREIGN KEY (`user_id`)  REFERENCES `user` (`id`)   ON DELETE CASCADE,
    CONSTRAINT `fk_rf_review` FOREIGN KEY (`review_id`) REFERENCES `review` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='影评收藏表';


-- ============================================================
-- 9. 勋章表 (badge)
-- ============================================================
DROP TABLE IF EXISTS `badge`;
CREATE TABLE `badge` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `name`        VARCHAR(50)  NOT NULL                COMMENT '勋章名称',
    `description` VARCHAR(200) DEFAULT NULL            COMMENT '勋章描述',
    `icon`        VARCHAR(20)  DEFAULT '🏅'           COMMENT '图标',
    `rule_type`   VARCHAR(50)  NOT NULL                COMMENT '规则类型: review_count/watched_count/favorite_count',
    `rule_value`  INT          NOT NULL                COMMENT '阈值',
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='勋章表';

-- 预置勋章
INSERT INTO badge (name, description, icon, rule_type, rule_value) VALUES
('影评新人', '发表第一篇影评', '✍️', 'review_count', 1),
('影评达人', '发表10篇影评', '📝', 'review_count', 10),
('黄金影评', '发表50篇影评', '🌟', 'review_count', 50),
('影视新手', '看过10部影视', '🎬', 'watched_count', 10),
('资深影迷', '看过50部影视', '🍿', 'watched_count', 50),
('观影狂人', '看过100部影视', '🔥', 'watched_count', 100),
('收藏新秀', '收藏10部影视', '💎', 'favorite_count', 10),
('收藏家', '收藏30部影视', '🏆', 'favorite_count', 30);

-- ============================================================
-- 10. 用户勋章表 (user_badge)
-- ============================================================
DROP TABLE IF EXISTS `user_badge`;
CREATE TABLE `user_badge` (
    `id`         BIGINT   NOT NULL AUTO_INCREMENT,
    `user_id`    BIGINT   NOT NULL,
    `badge_id`   BIGINT   NOT NULL,
    `awarded_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_badge` (`user_id`, `badge_id`),
    CONSTRAINT `fk_ub_user`  FOREIGN KEY (`user_id`)  REFERENCES `user` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_ub_badge` FOREIGN KEY (`badge_id`) REFERENCES `badge` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户勋章表';
