package com.j2ee.MovieManageSystem.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 状态标记请求
 */
@Data
public class StatusRequest {
    @NotNull(message = "影视剧ID不能为空")
    private Long movieId;

    @Pattern(regexp = "^(want_to_watch|watched)$", message = "状态值无效")
    private String status;
}
