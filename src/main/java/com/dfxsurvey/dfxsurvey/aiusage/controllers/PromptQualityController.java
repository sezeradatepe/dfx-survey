package com.dfxsurvey.dfxsurvey.aiusage.controllers;

import com.dfxsurvey.dfxsurvey.aiusage.dto.PromptQualityRequest;
import com.dfxsurvey.dfxsurvey.aiusage.dto.PromptQualityResponse;
import com.dfxsurvey.dfxsurvey.aiusage.services.IPromptQualityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// /admin altında: API maliyeti olduğu için yalnızca ADMIN token'ı ile erişilebilir.
@RestController
@RequestMapping("/admin/api/v1/ai-usage")
@RequiredArgsConstructor
public class PromptQualityController {

    private final IPromptQualityService promptQualityService;

    @PostMapping("/prompt-quality")
    public ResponseEntity<PromptQualityResponse> evaluatePrompt(@RequestBody PromptQualityRequest request) {
        return ResponseEntity.ok(promptQualityService.evaluate(request));
    }
}
