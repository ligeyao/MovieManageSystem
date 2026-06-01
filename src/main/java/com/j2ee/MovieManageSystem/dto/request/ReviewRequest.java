package com.j2ee.MovieManageSystem.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 影评请求
 */
@Data
public class ReviewRequest {
    @NotNull(message = "影视剧ID不能为空")
    private Long movieId;

    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分最低为1")
    @Max(value = 10, message = "评分最高为10")
    private Integer rating;

    private String content;
}
