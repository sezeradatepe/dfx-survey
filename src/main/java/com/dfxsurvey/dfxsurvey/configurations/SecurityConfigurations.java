package com.dfxsurvey.dfxsurvey.configurations;

import com.dfxsurvey.dfxsurvey.filters.JwtAuthFilter;
import com.dfxsurvey.dfxsurvey.filters.JwtErrorResponseWriter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfigurations {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {

        http.csrf(csrf -> csrf.disable());

        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/admin/login", "/user/login").permitAll()  // login JWT istemez
                .requestMatchers("/admin/**").hasRole("ADMIN")               // sadece admin token'ı
                .requestMatchers("/user/**").hasRole("USER")                 // sadece katılımcı token'ı
                .anyRequest().permitAll()                                    // swagger vs herkes
        );

        http.exceptionHandling(ex -> ex
                // Token hiç gönderilmemiş → 401
                .authenticationEntryPoint((request, response, e) ->
                        JwtErrorResponseWriter.writeUnauthorized(request, response, JwtErrorResponseWriter.MISSING_HEADER))
                // Token geçerli ama rol bu path için uygun değil → 403
                .accessDeniedHandler((request, response, e) ->
                        JwtErrorResponseWriter.writeForbidden(request, response, JwtErrorResponseWriter.ACCESS_DENIED))
        );

        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // JwtAuthFilter @Component olduğu için Spring Boot onu ayrıca servlet filtresi olarak da kaydeder.
    // Filtre yalnızca Security zincirinde çalışsın diye otomatik kaydı kapatıyoruz.
    @Bean
    public FilterRegistrationBean<JwtAuthFilter> jwtAuthFilterRegistration(JwtAuthFilter jwtAuthFilter) {
        FilterRegistrationBean<JwtAuthFilter> registration = new FilterRegistrationBean<>(jwtAuthFilter);
        registration.setEnabled(false);
        return registration;
    }
}
