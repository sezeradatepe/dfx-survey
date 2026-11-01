package com.dfxsurvey.dfxsurvey.aiusage.controllers;

import com.dfxsurvey.dfxsurvey.aiusage.exceptions.AiServiceException;
import com.dfxsurvey.dfxsurvey.exceptions.ValidationException;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Yalnızca aiusage controller'larına uygulanır; projenin geri kalanının davranışını değiştirmez.
@RestControllerAdvice(basePackageClasses = PromptQualityController.class)
public class AiUsageExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, String>> handleValidation(ValidationException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(AiServiceException.class)
    public ResponseEntity<Map<String, String>> handleAiService(AiServiceException e) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of("message", e.getMessage()));
    }
}
