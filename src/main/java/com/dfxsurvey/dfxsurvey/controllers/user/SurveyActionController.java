package com.dfxsurvey.dfxsurvey.controllers.user;

import com.dfxsurvey.dfxsurvey.controllers.dto.VoteRequestDto;
import com.dfxsurvey.dfxsurvey.controllers.dto.VoteRequestDto.AnswerDto;
import com.dfxsurvey.dfxsurvey.dao.entity.SurveyEntity;
import com.dfxsurvey.dfxsurvey.mapper.ServiceMapper;
import com.dfxsurvey.dfxsurvey.services.ISurveyService;
import com.dfxsurvey.dfxsurvey.services.IVoteService;
import com.dfxsurvey.dfxsurvey.services.impl.VoteServiceImpl;
import com.dfxsurvey.dfxsurvey.validators.impl.SurveyValidator;
import com.dfxsurvey.dfxsurvey.validators.impl.VoteRequestValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class SurveyActionController {

    private final ISurveyService surveyService;
    private final ServiceMapper serviceMapper;
    private final SurveyValidator surveyValidator;
    private final IVoteService voteService;
    private final VoteRequestValidator voteRequestValidator;

    @GetMapping("/api/v1/surveys/detail/{id}")
    public ResponseEntity<?> surveyDetail(@PathVariable Long id) {

        SurveyEntity survey = surveyService.getSurveyById(id);
        surveyValidator.validate(survey);

        return ResponseEntity.ok(survey);
    }

    @PostMapping("/api/v1/surveys/complete-survey")
    public ResponseEntity<?> completeSurvey(@RequestBody VoteRequestDto voteRequestDto, Authentication authentication) {
        voteRequestDto.setUserName(authentication.getName());
        SurveyEntity survey = surveyService.getSurveyById(voteRequestDto.getSurveyId());
        try{
        surveyValidator.validate(survey);
        voteRequestValidator.validate(voteRequestDto);
        voteService.submitVote(voteRequestDto, survey);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok(survey);
    }

}
