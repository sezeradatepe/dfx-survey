package com.dfxsurvey.dfxsurvey.controllers.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionStatisticsDto {
    private Long questionId;
    private String questionText;
    private List<LikertOptionCount> likertCounts;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LikertOptionCount {
        private Integer value;
        private String label;
        private int count;
    }
}
