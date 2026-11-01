package com.dfxsurvey.dfxsurvey.dao.repository;

import com.dfxsurvey.dfxsurvey.dao.entity.QuestionEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<QuestionEntity, Long> {

    // Belirli bir anketin sorularını getir
    List<QuestionEntity> findBySurveyId(Long surveyId);

    // Sadece LIKERT tipindeki soruları getir
    List<QuestionEntity> findBySurveyIdAndQuestionType(Long surveyId, String questionType);

}
