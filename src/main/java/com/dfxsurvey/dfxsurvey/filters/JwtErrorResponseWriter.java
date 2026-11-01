package com.dfxsurvey.dfxsurvey.filters;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;


public final class JwtErrorResponseWriter {

    public static final String MISSING_HEADER = "Authorization header bulunamadı. Lütfen 'Bearer <token>' formatında token gönderin.";
    public static final String INVALID_HEADER_FORMAT = "Authorization header 'Bearer <token>' formatında olmalıdır.";
    public static final String ACCESS_DENIED = "Bu işlem için yetkiniz yok.";

    private JwtErrorResponseWriter() {
    }

    // Token doğrulama sırasında oluşan exception'ı kullanıcıya gösterilecek mesaja çevirir
    public static String messageFor(Exception e) {
        if (e instanceof ExpiredJwtException) {
            return "Token süresi dolmuş. Lütfen tekrar giriş yapın.";
        }
        if (e instanceof SignatureException) {
            return "Token imzası geçersiz.";
        }
        if (e instanceof MalformedJwtException) {
            return "Token formatı geçersiz.";
        }
        if (e instanceof UnsupportedJwtException) {
            return "Desteklenmeyen token türü. Lütfen tekrar giriş yapın.";
        }
        if (e instanceof IllegalArgumentException) {
            return "Token boş olamaz.";
        }
        return "Token doğrulanamadı.";
    }

    public static void writeUnauthorized(HttpServletRequest request, HttpServletResponse response,
            String message) throws IOException {
        write(request, response, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized", message);
    }

    public static void writeForbidden(HttpServletRequest request, HttpServletResponse response,
            String message) throws IOException {
        write(request, response, HttpServletResponse.SC_FORBIDDEN, "Forbidden", message);
    }

    private static void write(HttpServletRequest request, HttpServletResponse response,
            int status, String error, String message) throws IOException {

        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{"
                + "\"timestamp\":\"" + LocalDateTime.now() + "\","
                + "\"status\":" + status + ","
                + "\"error\":\"" + error + "\","
                + "\"message\":\"" + escape(message) + "\","
                + "\"path\":\"" + escape(request.getRequestURI()) + "\""
                + "}");
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
