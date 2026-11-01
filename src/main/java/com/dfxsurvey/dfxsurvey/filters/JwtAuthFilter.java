package com.dfxsurvey.dfxsurvey.filters;

import com.dfxsurvey.dfxsurvey.utils.JwtUser;
import com.dfxsurvey.dfxsurvey.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Admin ve kullanıcı token'larının ikisini de doğrular; hangi path'e kimin erişeceğine
 * SecurityConfigurations'taki hasRole kuralları karar verir.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    // Login istekleri, elde süresi dolmuş bir token kalmış olsa bile engellenmesin
    private static final Set<String> LOGIN_PATHS = Set.of("/admin/login", "/user/login");

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return LOGIN_PATHS.contains(request.getRequestURI());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // Token yoksa isteği anonim olarak devam ettir; korumalı bir path ise
        // Security 401 döner (SecurityConfigurations → authenticationEntryPoint)
        if (authHeader == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!authHeader.startsWith("Bearer ")) {
            JwtErrorResponseWriter.writeUnauthorized(request, response, JwtErrorResponseWriter.INVALID_HEADER_FORMAT);
            return;
        }

        String token = authHeader.substring(7); // "Bearer <token>" -> Just token

        try {
            JwtUser jwtUser = jwtUtil.validateToken(token);

            // Kullanıcı adı principal olarak yazılır; controller'lar Authentication.getName() ile okur.
            // Rol token'dan gelir: ADMIN → ROLE_ADMIN, USER → ROLE_USER
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(jwtUser.username(), null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + jwtUser.role().name())));

            SecurityContextHolder.getContext().setAuthentication(authToken);
        } catch (Exception e) {
            JwtErrorResponseWriter.writeUnauthorized(request, response, JwtErrorResponseWriter.messageFor(e));
            return;
        }

        filterChain.doFilter(request, response);
    }
}
