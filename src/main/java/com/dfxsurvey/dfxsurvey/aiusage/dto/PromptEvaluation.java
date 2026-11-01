package com.dfxsurvey.dfxsurvey.aiusage.dto;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import java.util.List;

/**
 * Claude'un döndüğü değerlendirme. JSON şeması bu record'dan otomatik üretilir (structured outputs).
 */
public record PromptEvaluation(
        @JsonPropertyDescription("1-10 arası genel kalite puanı")
        int overallScore,
        @JsonPropertyDescription("Kriter bazında puanlar")
        List<CriterionScore> criteria,
        @JsonPropertyDescription("Prompt'un güçlü yönleri")
        List<String> strengths,
        @JsonPropertyDescription("Prompt'un eksik veya zayıf yönleri")
        List<String> weaknesses,
        @JsonPropertyDescription("Eksikleri gideren, iyileştirilmiş prompt önerisi")
        String improvedPrompt,
        @JsonPropertyDescription("Değerlendirmenin 1-2 cümlelik özeti")
        String summary) {

    public record CriterionScore(
            @JsonPropertyDescription("Kriter adı")
            String name,
            @JsonPropertyDescription("1-10 arası puan")
            int score,
            @JsonPropertyDescription("Puanın kısa gerekçesi")
            String comment) {
    }
}
