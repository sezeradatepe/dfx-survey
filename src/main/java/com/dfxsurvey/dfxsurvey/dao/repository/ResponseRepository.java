package com.dfxsurvey.dfxsurvey.dao.repository;

import com.dfxsurvey.dfxsurvey.dao.entity.CompletedSurveyEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ResponseRepository extends JpaRepository<CompletedSurveyEntity, Long> {

    // Kullanıcı bu ankete daha önce oy vermiş mi? (@Param adı sorgudaki :username ile aynı olmalı)
    @Query("SELECT c FROM CompletedSurveyEntity c WHERE c.survey.id = :surveyId AND c.user.username = :username")
    Optional<CompletedSurveyEntity> findBySurveyIdAndUsername(@Param("surveyId") Long surveyId,
            @Param("username") String username);

}
