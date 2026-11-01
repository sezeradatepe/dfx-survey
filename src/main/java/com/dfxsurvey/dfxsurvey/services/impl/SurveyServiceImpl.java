package com.dfxsurvey.dfxsurvey.services.impl;

import com.dfxsurvey.dfxsurvey.controllers.dto.CreateSurveyRequest;
import com.dfxsurvey.dfxsurvey.controllers.dto.FreeTextAnswerDto;
import com.dfxsurvey.dfxsurvey.controllers.dto.QuestionStatisticsDto;
import com.dfxsurvey.dfxsurvey.controllers.dto.SurveyDetailResultResponse;
import com.dfxsurvey.dfxsurvey.dao.entity.AnswerEntity;
import com.dfxsurvey.dfxsurvey.dao.entity.LikertOptionEntity;
import com.dfxsurvey.dfxsurvey.dao.entity.QuestionEntity;
import com.dfxsurvey.dfxsurvey.dao.entity.SurveyEntity;
import com.dfxsurvey.dfxsurvey.dao.repository.SurveyAnswerRepository;
import com.dfxsurvey.dfxsurvey.dao.repository.SurveyRepository;
import com.dfxsurvey.dfxsurvey.services.IMailService;
import com.dfxsurvey.dfxsurvey.services.ISurveyService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SurveyServiceImpl implements ISurveyService {

    private final SurveyRepository surveyRepository;
    private final IMailService mailService;
    private final SurveyAnswerRepository surveyAnswerRepository;


    @Override
    @Transactional
    public SurveyEntity createSurvey(CreateSurveyRequest request) {

        SurveyEntity survey = new SurveyEntity();
        survey.setTitle(request.getTitle());
        survey.setDescription(request.getDescription());
        survey.setExpiresAt(request.getExpiresAt());
        survey.setMailList(request.getMailList());

        // Soruları ekle
        if (CollectionUtils.isNotEmpty(request.getQuestions())) {
            for (CreateSurveyRequest.QuestionCreateRequest qReq : request.getQuestions()) {

                QuestionEntity question = new QuestionEntity();
                question.setSurvey(survey);
                question.setQuestionText(qReq.getQuestionText());
                question.setQuestionType(qReq.getQuestionType());
                question.setRequired(qReq.getRequired());

                // Likert seçenekleri varsa ekle
                if ("LIKERT".equalsIgnoreCase(qReq.getQuestionType())
                        && qReq.getLikertOptions() != null) {

                    for (CreateSurveyRequest.QuestionCreateRequest.LikertOptionRequest loReq :
                            qReq.getLikertOptions()) {

                        LikertOptionEntity lo = new LikertOptionEntity();
                        lo.setQuestion(question);
                        lo.setValue(loReq.getValue());
                        lo.setLabel(loReq.getLabel());

                        question.getLikertOptions().add(lo);
                    }
                }

                survey.getQuestions().add(question);
            }
        }

        SurveyEntity surveyEntity = surveyRepository.save(survey);
        surveyRepository.flush();  // commit yapılır

        try {
            mailService.sendSimpleMail(surveyEntity.getMailList(), surveyEntity.getTitle(),
                    surveyEntity.getDescription(), surveyEntity);
        } catch (Exception e) {
            log.debug("Mail gönderme işlemi esnasında bir hata oluştu. :( ", e);
        }
        return surveyEntity;
    }

    @Override
    public List<SurveyEntity> getActiveSurveys() {
        return surveyRepository.findActiveSurveys();
    }

    public SurveyEntity getSurveyById(Long id) {
        return surveyRepository.findById(id).orElse(null);
    }

    @Override
    public SurveyDetailResultResponse surveyResponse(Long id) {
        SurveyEntity survey = surveyRepository.findById(id).orElse(null);
        if (survey == null) {
            log.info("Aktif anket bulunamadı");
        }
        SurveyDetailResultResponse surveyDetailResultResponse = new SurveyDetailResultResponse();
        List<QuestionEntity> questionEntities = survey.getQuestions();
        List<QuestionStatisticsDto> statistics = new ArrayList<>();
        List<FreeTextAnswerDto> freeTextAnswerDtos = new ArrayList<>();

        for (QuestionEntity questionEntity : questionEntities) {
            List<AnswerEntity> answerEntities = surveyAnswerRepository.findByQuestionId(
                    questionEntity.getId());
            if(answerEntities.isEmpty()){
                continue;
            }
            if ("LIKERT".equalsIgnoreCase(questionEntity.getQuestionType())) {
                Map<Integer, Integer> countMap = new HashMap<>();

                // Önce tüm seçenekleri 0 ile başlat
                for (LikertOptionEntity option : questionEntity.getLikertOptions()) {
                    countMap.put(option.getValue(), 0);
                }

                // Cevapları say
                if (CollectionUtils.isNotEmpty(answerEntities)) {
                    for (AnswerEntity answerEntity : answerEntities) {
                        Integer likertValue = answerEntity.getLikertValue();
                        if (likertValue != null && countMap.containsKey(likertValue)) {
                            countMap.put(likertValue, countMap.get(likertValue) + 1);
                        }
                    }
                }

                // DTO'ya dönüştür
                List<QuestionStatisticsDto.LikertOptionCount> likertCounts = new ArrayList<>();
                for (LikertOptionEntity option : questionEntity.getLikertOptions()) {
                    likertCounts.add(new QuestionStatisticsDto.LikertOptionCount(
                            option.getValue(),
                            option.getLabel(),
                            countMap.get(option.getValue())
                    ));
                }

                statistics.add(new QuestionStatisticsDto(
                        questionEntity.getId(),
                        questionEntity.getQuestionText(),
                        likertCounts
                ));
            } else {

                answerEntities.stream().forEach(answ -> freeTextAnswerDtos.add(
                        new FreeTextAnswerDto(questionEntity.getQuestionText(),answ.getFreetextAnswer())
                ));
            }
        }

        surveyDetailResultResponse.setStatistics(statistics);
        surveyDetailResultResponse.setFreeTextAnswers(freeTextAnswerDtos);
        return surveyDetailResultResponse;

    }
}
