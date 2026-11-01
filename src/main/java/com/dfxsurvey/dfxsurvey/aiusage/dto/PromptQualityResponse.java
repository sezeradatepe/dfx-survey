package com.dfxsurvey.dfxsurvey.aiusage.dto;

/**
 * İstemciye dönen cevap: değerlendirme + bu istek için harcanan token miktarı.
 */
public record PromptQualityResponse(PromptEvaluation evaluation, long inputTokens, long outputTokens) {
}
