package com.dfxsurvey.dfxsurvey.services;

import com.dfxsurvey.dfxsurvey.controllers.dto.VoteRequestDto;
import com.dfxsurvey.dfxsurvey.dao.entity.SurveyEntity;

public interface IVoteService {
    // Oy veren kullanıcı request.getUserName() ile gelir (controller token'dan doldurur)
    void submitVote(VoteRequestDto request, SurveyEntity survey);
}
