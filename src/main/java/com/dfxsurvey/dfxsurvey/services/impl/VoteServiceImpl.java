package com.dfxsurvey.dfxsurvey.services.impl;

import com.dfxsurvey.dfxsurvey.controllers.dto.VoteRequestDto;
import com.dfxsurvey.dfxsurvey.dao.entity.AnswerEntity;
import com.dfxsurvey.dfxsurvey.dao.entity.CompletedSurveyEntity;
import com.dfxsurvey.dfxsurvey.dao.entity.QuestionEntity;
import com.dfxsurvey.dfxsurvey.dao.entity.SurveyEntity;
import com.dfxsurvey.dfxsurvey.dao.entity.TokenEntity;
import com.dfxsurvey.dfxsurvey.dao.entity.UserEntity;
import com.dfxsurvey.dfxsurvey.dao.repository.QuestionRepository;
import com.dfxsurvey.dfxsurvey.dao.repository.ResponseRepository;
import com.dfxsurvey.dfxsurvey.dao.repository.UserRepository;
import com.dfxsurvey.dfxsurvey.exceptions.ValidationException;
import com.dfxsurvey.dfxsurvey.services.IVoteService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VoteServiceImpl implements IVoteService {

    private final QuestionRepository questionRepository;
    private final ResponseRepository responseRepository;
    private final UserRepository userRepository;


    @Override
    @Transactional
    public void submitVote(VoteRequestDto request, SurveyEntity survey) {

        UserEntity user = userRepository.findUserEntityByUsername(request.getUserName());
        if (user == null) {
            throw new ValidationException("Oy veren kullanıcı bulunamadı.");
        }

        CompletedSurveyEntity completedSurveyEntity = new CompletedSurveyEntity();
        completedSurveyEntity.setSubmittedAt(LocalDateTime.now());
        completedSurveyEntity.setSurvey(survey);
        completedSurveyEntity.setUser(user);   // çift oy kontrolü (VoteRequestValidator) bu alana bakar
        completedSurveyEntity.setAnswers(new ArrayList<>());

        TokenEntity tokenEntity = new TokenEntity();
        tokenEntity.setToken(request.getToken());
        tokenEntity.setSurvey(survey);
        tokenEntity.setUser(user);
        completedSurveyEntity.setToken(tokenEntity);

        for (VoteRequestDto.AnswerDto answerDto : request.getAnswers()) {
            Optional<QuestionEntity> questionEntityOptional = questionRepository.findById(answerDto.getQuestionId());
            if (questionEntityOptional.isPresent()) {
                AnswerEntity answer = new AnswerEntity();
                answer.setResponse(completedSurveyEntity);   // cevap hangi oya ait
                answer.setQuestion(questionEntityOptional.get());
                answer.setLikertValue(answerDto.getLikertValue());
                answer.setFreetextAnswer(answerDto.getTextAnswer());
                completedSurveyEntity.getAnswers().add(answer);
            }
        }

        // Tek save: cevaplar (cascade ALL) ve token (cascade PERSIST) birlikte kaydedilir
        responseRepository.save(completedSurveyEntity);
    }
}
