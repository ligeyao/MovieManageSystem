package com.j2ee.MovieManageSystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 发布者回复请求
 */
@Data
public class ReplyRequest {
    @NotBlank(message = "回复内容不能为空")
    private String reply;
}
