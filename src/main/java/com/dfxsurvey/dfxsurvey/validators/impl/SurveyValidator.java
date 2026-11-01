package com.dfxsurvey.dfxsurvey.validators.impl;

import com.dfxsurvey.dfxsurvey.controllers.dto.SurveyDto;
import com.dfxsurvey.dfxsurvey.dao.entity.SurveyEntity;
import com.dfxsurvey.dfxsurvey.exceptions.ValidationException;
import com.dfxsurvey.dfxsurvey.validators.Validator;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class SurveyValidator implements Validator<SurveyEntity> {

    @Override
    public void validate(SurveyEntity target) {

        if(target == null) {
            throw new ValidationException("Aktif anket bulunamamıştır.");
        }

        if(LocalDateTime.now().isAfter(target.getExpiresAt())) {
            throw new ValidationException("Anket tarihi dolmuştur. Oy veremezsiniz.");
        }

    }
}
