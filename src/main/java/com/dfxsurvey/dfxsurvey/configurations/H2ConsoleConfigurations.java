package com.dfxsurvey.dfxsurvey.configurations;

import org.h2.server.web.JakartaWebServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Yalnızca h2 profilinde http://localhost:8080/h2-console adresinde H2 web konsolunu açar.
 * Konsol frame'ler içinde çalıştığı için varsayılan X-Frame-Options: DENY başlığıyla açılmaz;
 * bu yüzden konsol path'ine özel ayrı bir güvenlik zinciri tanımlanır. Diğer istekler
 * SecurityConfigurations'taki ana zincirden geçer.
 */
@Configuration
@Profile("h2")
public class H2ConsoleConfigurations {
    @Bean
    public ServletRegistrationBean<JakartaWebServlet> h2ConsoleServlet() {
        return new ServletRegistrationBean<>(new JakartaWebServlet(), "/h2-console/*");
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain h2ConsoleFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/h2-console/**");
        http.csrf(csrf -> csrf.disable());
        http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));
        http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }
}
