package com.dfxsurvey.dfxsurvey.services.impl;

import com.dfxsurvey.dfxsurvey.dao.entity.UserEntity;
import com.dfxsurvey.dfxsurvey.dao.repository.UserRepository;
import com.dfxsurvey.dfxsurvey.services.ILoginService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements ILoginService {


    private final UserRepository userRepository;

    @Override
    public Optional<UserEntity> doLogin(String username, String password) {

        UserEntity user = userRepository.findUserEntityByUsername(username);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            return Optional.empty();
        }

        return Optional.of(user);
    }
}
