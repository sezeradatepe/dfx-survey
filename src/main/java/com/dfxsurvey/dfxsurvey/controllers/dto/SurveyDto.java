package com.dfxsurvey.dfxsurvey.controllers.dto;

import lombok.Data;

@Data
public class SurveyDto {

    private Long id;
    private String title;
    private String description;
    private String expiresAt;
    private String mailList;

}
