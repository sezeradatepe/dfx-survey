package com.dfxsurvey.dfxsurvey.dao.repository;

import com.dfxsurvey.dfxsurvey.dao.entity.SurveyEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SurveyRepository extends JpaRepository<SurveyEntity, Long> {

    // ID ile survey bul
    Optional<SurveyEntity> findById(Long id);

    // Tüm aktif anketleri getir
    @Query("SELECT s FROM SurveyEntity s WHERE s.expiresAt > CURRENT_TIMESTAMP()")
    List<SurveyEntity> findActiveSurveys();

    // Anket başlığına göre arama
    List<SurveyEntity> findByTitleContainingIgnoreCase(String title);

}
