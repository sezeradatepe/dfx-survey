package com.dfxsurvey.dfxsurvey.controllers.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import lombok.Data;

@Data
public class VoteRequestDto {

    private Long surveyId;
    private String token;
    private List<AnswerDto> answers;
    @JsonIgnore  // istemci gönderemez; controller token'daki kullanıcı adıyla doldurur
    private String userName;

    @Data
    public static class AnswerDto {
        private Long questionId;
        private Integer likertValue;   // likert soruları için
        private String textAnswer;     // freetext soruları için
    }

}
