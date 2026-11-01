package com.dfxsurvey.dfxsurvey.controllers.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class CreateSurveyRequest {
    private String title;
    private String description;
    private LocalDateTime expiresAt;
    private String mailList;

    private List<QuestionCreateRequest> questions;

    @Data
    public static class QuestionCreateRequest {
        private String questionText;
        private String questionType;  // FREETEXT veya LIKERT
        private Boolean required = true;

        private List<LikertOptionRequest> likertOptions;

        @Data
        public static class LikertOptionRequest {
            private Integer value;
            private String label;
        }
    }
}
