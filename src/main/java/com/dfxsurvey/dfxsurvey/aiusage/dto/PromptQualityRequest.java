package com.dfxsurvey.dfxsurvey.aiusage.dto;

import lombok.Data;

@Data
public class PromptQualityRequest {

    private String prompt;    // kalitesi ölçülecek prompt
    private String purpose;   // opsiyonel: prompt'un ne için kullanılacağı (değerlendirmeye bağlam sağlar)
}
