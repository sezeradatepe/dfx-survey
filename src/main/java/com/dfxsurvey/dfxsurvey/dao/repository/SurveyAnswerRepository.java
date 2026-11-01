package com.dfxsurvey.dfxsurvey.dao.repository;

import com.dfxsurvey.dfxsurvey.dao.entity.AnswerEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SurveyAnswerRepository  extends JpaRepository<AnswerEntity, Long> {

    List<AnswerEntity> findByQuestionId(Long questionId);

}
