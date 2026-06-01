package com.j2ee.MovieManageSystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 统计图表响应
 */
@Data
@AllArgsConstructor
public class StatsResponse {
    private List<String> labels;
    private List<Integer> values;
}
