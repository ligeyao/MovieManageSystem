package com.j2ee.MovieManageSystem.mapper;

import com.j2ee.MovieManageSystem.entity.User;
import org.apache.ibatis.annotations.*;

/**
 * 用户 Mapper
 */
@Mapper
public interface UserMapper {

    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUsername(String username);

    @Select("SELECT * FROM user WHERE id = #{id}")
    User selectById(Long id);

    @Insert("INSERT INTO user (username, password, role, status) " +
            "VALUES (#{username}, #{password}, #{role}, 'active')")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Update("UPDATE user SET role = #{role}, status = #{status} WHERE id = #{id}")
    int updateRoleStatus(User user);

    @Delete("DELETE FROM user WHERE id = #{id}")
    int deleteById(Long id);

    @Select("<script>" +
            "SELECT * FROM user WHERE 1=1 " +
            "<if test='keyword != null and keyword != \"\"'>AND username LIKE CONCAT('%',#{keyword},'%') </if>" +
            "<if test='role != null and role != \"\"'>AND role = #{role} </if>" +
            "<if test='status != null and status != \"\"'>AND status = #{status} </if>" +
            "ORDER BY created_at DESC</script>")
    java.util.List<User> selectPage(@Param("keyword") String keyword,
                                     @Param("role") String role,
                                     @Param("status") String status);

    @Select("SELECT DATE(created_at) AS label, COUNT(*) AS cnt " +
            "FROM user " +
            "WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) " +
            "GROUP BY DATE(created_at) " +
            "ORDER BY label")
    java.util.List<java.util.Map<String, Object>> countByDayLast30Days();
}
