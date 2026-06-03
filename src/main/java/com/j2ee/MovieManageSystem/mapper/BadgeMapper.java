package com.j2ee.MovieManageSystem.mapper;

import com.j2ee.MovieManageSystem.entity.Badge;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BadgeMapper {

    @Select("SELECT * FROM badge ORDER BY created_at")
    List<Badge> selectAll();

    @Insert("INSERT INTO badge (name, description, icon, rule_type, rule_value) " +
            "VALUES (#{name}, #{description}, #{icon}, #{ruleType}, #{ruleValue})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Badge badge);

    @Update("UPDATE badge SET name=#{name},description=#{description},icon=#{icon}," +
            "rule_type=#{ruleType},rule_value=#{ruleValue} WHERE id=#{id}")
    int update(Badge badge);

    @Delete("DELETE FROM badge WHERE id=#{id}")
    int delete(Long id);

    // ---- 用户勋章 ----

    @Insert("INSERT IGNORE INTO user_badge (user_id, badge_id) VALUES (#{userId}, #{badgeId})")
    int awardBadge(@Param("userId") Long userId, @Param("badgeId") Long badgeId);

    @Select("SELECT badge_id FROM user_badge WHERE user_id=#{userId}")
    List<Long> selectBadgeIdsByUser(Long userId);

    @Select("SELECT b.* FROM badge b JOIN user_badge ub ON b.id=ub.badge_id WHERE ub.user_id=#{userId}")
    List<Badge> selectBadgesByUser(Long userId);
}
