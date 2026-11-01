package com.dfxsurvey.dfxsurvey.dao.repository;

import com.dfxsurvey.dfxsurvey.dao.entity.QuestionEntity;
import com.dfxsurvey.dfxsurvey.dao.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findUserEntityByUsername(String username);

}
