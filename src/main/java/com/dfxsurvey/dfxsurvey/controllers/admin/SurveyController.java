package com.dfxsurvey.dfxsurvey.controllers.admin;

import com.dfxsurvey.dfxsurvey.controllers.dto.CreateSurveyRequest;
import com.dfxsurvey.dfxsurvey.controllers.dto.SurveyDetailResultResponse;
import com.dfxsurvey.dfxsurvey.dao.entity.SurveyEntity;
import com.dfxsurvey.dfxsurvey.mapper.ServiceMapper;
import com.dfxsurvey.dfxsurvey.services.ISurveyService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class SurveyController {

    private final ISurveyService surveyService;
    private final ServiceMapper serviceMapper;

    @PostMapping("/api/v1/surveys")
    public ResponseEntity<?> createSurvey(@RequestBody CreateSurveyRequest request) {
        SurveyEntity survey = surveyService.createSurvey(request);
        return ResponseEntity.ok(survey.getId());
    }

    @GetMapping("/api/v1/surveys/active")
    public ResponseEntity<?> getAllActiveSurveys() {
        List<SurveyEntity> surveys = surveyService.getActiveSurveys();
        return ResponseEntity.ok(serviceMapper.toSurveyDtos(surveys));
    }

    @GetMapping("/api/v1/surveys/response/{id}")
    public ResponseEntity<?> surveyResponse(@PathVariable Long id) {
        SurveyDetailResultResponse surveyResultResponse = surveyService.surveyResponse(id);
        return ResponseEntity.ok(surveyResultResponse);
    }

    @GetMapping("/api/v1/surveys/detail/{id}")
    public ResponseEntity<?> surveyDetail(@PathVariable Long id) {
        SurveyEntity survey = surveyService.getSurveyById(id);
        return ResponseEntity.ok(survey);
    }


}
