package com.dfxsurvey.dfxsurvey.controllers.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.dfxsurvey.dfxsurvey.controllers.dto.VoteRequestDto;
import com.dfxsurvey.dfxsurvey.dao.entity.SurveyEntity;
import com.dfxsurvey.dfxsurvey.mapper.ServiceMapper;
import com.dfxsurvey.dfxsurvey.services.ISurveyService;
import com.dfxsurvey.dfxsurvey.services.IVoteService;
import com.dfxsurvey.dfxsurvey.validators.impl.SurveyValidator;
import com.dfxsurvey.dfxsurvey.validators.impl.VoteRequestValidator;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import tools.jackson.databind.json.JsonMapper;

@ExtendWith(MockitoExtension.class)
class SurveyActionControllerTest {

    @Mock private ISurveyService surveyService;
    @Mock private ServiceMapper serviceMapper;
    @Mock private SurveyValidator surveyValidator;
    @Mock private IVoteService voteService;
    @Mock private VoteRequestValidator voteRequestValidator;

    @InjectMocks
    private SurveyActionController controller;

    @Test
    void complete_survey_takes_username_from_token_not_from_request() {
        SurveyEntity survey = new SurveyEntity();
        when(surveyService.getSurveyById(1L)).thenReturn(survey);

        VoteRequestDto request = new VoteRequestDto();
        request.setSurveyId(1L);
        request.setAnswers(List.of());
        request.setUserName("baskasi");   // istemci başka birinin adını vermeye çalışsa bile

        // JwtAuthFilter'ın SecurityContext'e yazdığı kimliğin karşılığı
        var authentication = new UsernamePasswordAuthenticationToken("katilimci", null, List.of());

        controller.completeSurvey(request, authentication);

        ArgumentCaptor<VoteRequestDto> captor = ArgumentCaptor.forClass(VoteRequestDto.class);
        verify(voteRequestValidator).validate(captor.capture());
        assertThat(captor.getValue().getUserName()).isEqualTo("katilimci");
        verify(voteService).submitVote(eq(request), eq(survey));
    }

    @Test
    void user_name_in_request_body_is_ignored() {
        String json = "{\"surveyId\":1,\"userName\":\"baskasi\",\"answers\":[]}";

        VoteRequestDto dto = JsonMapper.builder().build().readValue(json, VoteRequestDto.class);

        assertThat(dto.getSurveyId()).isEqualTo(1L);
        assertThat(dto.getUserName()).isNull();
    }
}
