package com.j2ee.MovieManageSystem.util;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 密码工具类（SHA-256 + 随机盐）
 */
@Component
public class PasswordUtil {

    /**
     * 加密密码：SHA-256(salt + password)，salt 拼接在密文前面
     */
    public String encode(String rawPassword) {
        byte[] salt = generateSalt();
        byte[] hash = sha256(salt, rawPassword.getBytes(StandardCharsets.UTF_8));
        // 格式：base64(salt) + ":" + base64(hash)
        return Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash);
    }

    /**
     * 校验密码
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        String[] parts = encodedPassword.split(":");
        if (parts.length != 2) return false;
        byte[] salt = Base64.getDecoder().decode(parts[0]);
        byte[] expectedHash = Base64.getDecoder().decode(parts[1]);
        byte[] actualHash = sha256(salt, rawPassword.getBytes(StandardCharsets.UTF_8));
        return MessageDigest.isEqual(expectedHash, actualHash);
    }

    private byte[] generateSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    private byte[] sha256(byte[] salt, byte[] password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            return md.digest(password);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
