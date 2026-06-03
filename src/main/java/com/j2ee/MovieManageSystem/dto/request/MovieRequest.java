package com.j2ee.MovieManageSystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 影视剧新增/编辑请求
 */
@Data
public class MovieRequest {
    @NotBlank(message = "中文片名不能为空")
    private String titleCn;

    private String titleEn;
    private String posterUrl;

    @NotBlank(message = "导演不能为空")
    private String director;

    private String actors;

    @NotBlank(message = "类型不能为空")
    private String genre;

    @NotNull(message = "年代不能为空")
    private Integer year;

    @NotBlank(message = "国家不能为空")
    private String country;

    @NotBlank(message = "语言不能为空")
    private String language;

    @NotNull(message = "片长不能为空")
    private Integer duration;

    private String description;
    private String platform;
    private String awards;
    private String type;    // movie / tv
}
