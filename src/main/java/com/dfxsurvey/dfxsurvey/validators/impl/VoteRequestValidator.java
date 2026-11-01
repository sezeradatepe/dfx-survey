package com.dfxsurvey.dfxsurvey.validators.impl;

import com.dfxsurvey.dfxsurvey.controllers.dto.VoteRequestDto;
import com.dfxsurvey.dfxsurvey.dao.entity.CompletedSurveyEntity;
import com.dfxsurvey.dfxsurvey.dao.repository.ResponseRepository;
import com.dfxsurvey.dfxsurvey.exceptions.ValidationException;
import com.dfxsurvey.dfxsurvey.validators.Validator;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VoteRequestValidator implements Validator<VoteRequestDto> {

    private final ResponseRepository responseRepository;


    @Override
    public void validate(VoteRequestDto target) {

      Optional<CompletedSurveyEntity> completedSurveyEntity =
              responseRepository.findBySurveyIdAndUsername(target.getSurveyId(), target.getUserName());

      if(completedSurveyEntity.isPresent()) {
          throw new ValidationException("Ankete cevap verdiğiniz için tekrar yanıt gönderemezsiniz.");
      }
    }
}
