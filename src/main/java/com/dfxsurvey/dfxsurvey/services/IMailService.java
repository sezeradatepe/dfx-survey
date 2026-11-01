package com.dfxsurvey.dfxsurvey.services;

import com.dfxsurvey.dfxsurvey.dao.entity.SurveyEntity;

public interface IMailService {

    void sendSimpleMail(String to, String subject, String text, SurveyEntity surveyEntity);

}
