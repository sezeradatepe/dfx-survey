package com.dfxsurvey.dfxsurvey.aiusage.services;

import com.dfxsurvey.dfxsurvey.aiusage.dto.PromptQualityRequest;
import com.dfxsurvey.dfxsurvey.aiusage.dto.PromptQualityResponse;

public interface IPromptQualityService {

    PromptQualityResponse evaluate(PromptQualityRequest request);
}
