package com.dfxsurvey.dfxsurvey.services;

import com.dfxsurvey.dfxsurvey.dao.entity.UserEntity;
import java.util.Optional;

public interface ILoginService {

    // Kullanıcı adı ve şifre doğruysa kullanıcıyı döner; rol kontrolü çağıran tarafa aittir
    Optional<UserEntity> doLogin(String username, String password);

}
