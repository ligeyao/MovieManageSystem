package com.j2ee.MovieManageSystem.interceptor;

/**
 * 当前用户工具类（ThreadLocal 实现）
 * 由 LoginInterceptor 在请求进入时设置，请求结束后清除
 */
public class CurrentUser {

    private static final ThreadLocal<UserInfo> USER_HOLDER = new ThreadLocal<>();

    public static void set(UserInfo user) {
        USER_HOLDER.set(user);
    }

    public static UserInfo get() {
        UserInfo user = USER_HOLDER.get();
        if (user == null) {
            throw new RuntimeException("未登录或 Token 已过期");
        }
        return user;
    }

    public static Long getUserId() {
        return get().userId();
    }

    public static String getUsername() {
        return get().username();
    }

    public static String getRole() {
        return get().role();
    }

    public static void remove() {
        USER_HOLDER.remove();
    }

    public record UserInfo(Long userId, String username, String role) {}
}
