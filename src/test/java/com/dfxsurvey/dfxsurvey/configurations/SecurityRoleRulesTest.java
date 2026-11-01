package com.dfxsurvey.dfxsurvey.configurations;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dfxsurvey.dfxsurvey.dao.entity.Role;
import com.dfxsurvey.dfxsurvey.filters.JwtAuthFilter;
import com.dfxsurvey.dfxsurvey.utils.JwtUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Veritabanı gerektirmeden sadece güvenlik katmanını (SecurityConfigurations + JwtAuthFilter + JwtUtil)
 * ayağa kaldırır ve rol kurallarını sahte endpoint'ler üzerinden doğrular.
 */
@SpringJUnitWebConfig(classes = {
        SecurityConfigurations.class, JwtAuthFilter.class, JwtUtil.class,
        SecurityRoleRulesTest.TestConfig.class, SecurityRoleRulesTest.PingController.class})
class SecurityRoleRulesTest {

    @Configuration
    @EnableWebMvc
    @EnableWebSecurity
    static class TestConfig {
    }

    @RestController
    static class PingController {
        @GetMapping("/admin/api/v1/ping")
        String adminPing() { return "admin-ok"; }

        @GetMapping("/user/api/v1/ping")
        String userPing() { return "user-ok"; }

        @PostMapping("/user/login")
        String userLogin() { return "login-ok"; }
    }

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private JwtUtil jwtUtil;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }

    private String bearer(Role role) {
        return "Bearer " + jwtUtil.generateToken("test-" + role.name().toLowerCase(), role);
    }

    @Test
    void admin_token_can_access_admin_endpoints() throws Exception {
        mockMvc.perform(get("/admin/api/v1/ping").header("Authorization", bearer(Role.ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().string("admin-ok"));
    }

    @Test
    void admin_token_cannot_access_user_endpoints() throws Exception {
        mockMvc.perform(get("/user/api/v1/ping").header("Authorization", bearer(Role.ADMIN)))
                .andExpect(status().isForbidden())
                .andExpect(content().string(containsString("Bu işlem için yetkiniz yok.")));
    }

    @Test
    void user_token_can_access_user_endpoints() throws Exception {
        mockMvc.perform(get("/user/api/v1/ping").header("Authorization", bearer(Role.USER)))
                .andExpect(status().isOk())
                .andExpect(content().string("user-ok"));
    }

    @Test
    void user_token_cannot_access_admin_endpoints() throws Exception {
        mockMvc.perform(get("/admin/api/v1/ping").header("Authorization", bearer(Role.USER)))
                .andExpect(status().isForbidden())
                .andExpect(content().string(containsString("Bu işlem için yetkiniz yok.")));
    }

    @Test
    void missing_token_returns_401_with_message() throws Exception {
        mockMvc.perform(get("/admin/api/v1/ping"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString("Authorization header bulunamadı")));
        mockMvc.perform(get("/user/api/v1/ping"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void non_bearer_header_returns_401() throws Exception {
        mockMvc.perform(get("/user/api/v1/ping").header("Authorization", "Basic abc"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString("'Bearer <token>' formatında olmalıdır")));
    }

    @Test
    void token_without_role_claim_is_rejected() throws Exception {
        // Rol değişikliğinden önce üretilmiş token'ları temsil eder
        String legacyToken = Jwts.builder()
                .subject("admin")
                .expiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(Keys.hmacShaKeyFor("MY_SUPER_LONG_SECRET_KEY_ADAX_123456".getBytes(StandardCharsets.UTF_8)))
                .compact();

        mockMvc.perform(get("/admin/api/v1/ping").header("Authorization", "Bearer " + legacyToken))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString("Desteklenmeyen token türü")));
    }

    @Test
    void login_is_not_blocked_by_a_stale_token() throws Exception {
        mockMvc.perform(post("/user/login").header("Authorization", "Bearer bozuk.token"))
                .andExpect(status().isOk())
                .andExpect(content().string("login-ok"));
    }
}
