package com.dfxsurvey.dfxsurvey.dao.repository;

import com.dfxsurvey.dfxsurvey.dao.entity.LikertOptionEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikertOptionRepository extends JpaRepository<LikertOptionEntity, Long> {

    List<LikertOptionEntity> findByQuestionId(Long questionId);

}
