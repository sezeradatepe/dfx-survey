package com.dfxsurvey.dfxsurvey.services;

import com.dfxsurvey.dfxsurvey.controllers.dto.CreateSurveyRequest;
import com.dfxsurvey.dfxsurvey.controllers.dto.SurveyDetailResultResponse;
import com.dfxsurvey.dfxsurvey.dao.entity.SurveyEntity;
import java.util.List;

public interface ISurveyService {

    SurveyEntity createSurvey(CreateSurveyRequest request);

    List<SurveyEntity> getActiveSurveys();

    SurveyEntity getSurveyById(Long id);

    SurveyDetailResultResponse surveyResponse(Long id);
}
