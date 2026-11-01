package com.dfxsurvey.dfxsurvey.controllers.dto;


import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SurveyDetailResultResponse {
    private List<QuestionStatisticsDto> statistics;
    private List<FreeTextAnswerDto> freeTextAnswers;

}
