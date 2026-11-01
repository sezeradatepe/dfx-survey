package com.dfxsurvey.dfxsurvey.utils;

import com.dfxsurvey.dfxsurvey.dao.entity.Role;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    private static final String ROLE_CLAIM = "role";

    private final String SECRET = "MY_SUPER_LONG_SECRET_KEY_ADAX_123456";

    public String generateToken(String username, Role role) {
        return Jwts.builder()
                .setSubject(username)
                .claim(ROLE_CLAIM, role.name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 24*60*60*1000)) // 1 gün
                .signWith(getSigningKey())
                .compact();
    }

    public JwtUser validateToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        // Rol bilgisi olmayan (eski) veya bilinmeyen rollü token'lar kabul edilmez
        String role = claims.get(ROLE_CLAIM, String.class);
        if (role == null) {
            throw new UnsupportedJwtException("Token rol bilgisi içermiyor.");
        }
        try {
            return new JwtUser(claims.getSubject(), Role.valueOf(role));
        } catch (IllegalArgumentException e) {
            throw new UnsupportedJwtException("Bilinmeyen rol: " + role);
        }
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }
}
