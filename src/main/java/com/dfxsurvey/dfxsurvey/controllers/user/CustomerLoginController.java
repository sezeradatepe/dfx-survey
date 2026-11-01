package com.dfxsurvey.dfxsurvey.controllers.user;

import com.dfxsurvey.dfxsurvey.dao.entity.Role;
import com.dfxsurvey.dfxsurvey.dao.entity.UserEntity;
import com.dfxsurvey.dfxsurvey.services.ILoginService;
import com.dfxsurvey.dfxsurvey.utils.JwtUtil;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class CustomerLoginController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ILoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username,
            @RequestParam String password) {

        Optional<UserEntity> user = loginService.doLogin(username, password);

        if (user.isEmpty()) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        if (user.get().getRole() != Role.USER) {
            return ResponseEntity.status(403).body("Bu giriş yalnızca katılımcılar içindir.");
        }

        String token = jwtUtil.generateToken(username, Role.USER);

        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + token)
                .body("Login successful");
    }


}
