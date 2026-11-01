package com.dfxsurvey.dfxsurvey.validators.impl;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.dfxsurvey.dfxsurvey.controllers.dto.VoteRequestDto;
import com.dfxsurvey.dfxsurvey.dao.entity.CompletedSurveyEntity;
import com.dfxsurvey.dfxsurvey.dao.repository.ResponseRepository;
import com.dfxsurvey.dfxsurvey.exceptions.ValidationException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VoteRequestValidatorTest {

    @Mock
    private ResponseRepository responseRepository;

    @InjectMocks
    private VoteRequestValidator validator;

    private VoteRequestDto request() {
        VoteRequestDto request = new VoteRequestDto();
        request.setSurveyId(7L);
        request.setUserName("katilimci");
        return request;
    }

    @Test
    void should_reject_user_who_already_voted() {
        when(responseRepository.findBySurveyIdAndUsername(7L, "katilimci"))
                .thenReturn(Optional.of(new CompletedSurveyEntity()));

        assertThatThrownBy(() -> validator.validate(request()))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Ankete cevap verdiğiniz için tekrar yanıt gönderemezsiniz.");
    }

    @Test
    void should_allow_first_vote() {
        when(responseRepository.findBySurveyIdAndUsername(7L, "katilimci")).thenReturn(Optional.empty());

        assertThatCode(() -> validator.validate(request())).doesNotThrowAnyException();
    }
}
