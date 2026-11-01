package com.dfxsurvey.dfxsurvey.services.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.dfxsurvey.dfxsurvey.controllers.dto.VoteRequestDto;
import com.dfxsurvey.dfxsurvey.dao.entity.CompletedSurveyEntity;
import com.dfxsurvey.dfxsurvey.dao.entity.QuestionEntity;
import com.dfxsurvey.dfxsurvey.dao.entity.SurveyEntity;
import com.dfxsurvey.dfxsurvey.dao.entity.UserEntity;
import com.dfxsurvey.dfxsurvey.dao.repository.QuestionRepository;
import com.dfxsurvey.dfxsurvey.dao.repository.ResponseRepository;
import com.dfxsurvey.dfxsurvey.dao.repository.UserRepository;
import com.dfxsurvey.dfxsurvey.exceptions.ValidationException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VoteServiceImplTest {

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private ResponseRepository responseRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private VoteServiceImpl voteService;

    private VoteRequestDto.AnswerDto answer(Long questionId, Integer likert, String text) {
        VoteRequestDto.AnswerDto dto = new VoteRequestDto.AnswerDto();
        dto.setQuestionId(questionId);
        dto.setLikertValue(likert);
        dto.setTextAnswer(text);
        return dto;
    }

    private VoteRequestDto request(String userName, VoteRequestDto.AnswerDto... answers) {
        VoteRequestDto request = new VoteRequestDto();
        request.setSurveyId(1L);
        request.setToken("tkn-1");
        request.setUserName(userName);
        request.setAnswers(List.of(answers));
        return request;
    }

    @Test
    void should_link_vote_token_and_answers_to_the_voting_user() {
        UserEntity user = new UserEntity();
        user.setUsername("katilimci");
        SurveyEntity survey = new SurveyEntity();
        QuestionEntity likertQuestion = new QuestionEntity();
        QuestionEntity freeTextQuestion = new QuestionEntity();

        when(userRepository.findUserEntityByUsername("katilimci")).thenReturn(user);
        when(questionRepository.findById(10L)).thenReturn(Optional.of(likertQuestion));
        when(questionRepository.findById(11L)).thenReturn(Optional.of(freeTextQuestion));

        voteService.submitVote(request("katilimci", answer(10L, 5, null), answer(11L, null, "Harika")), survey);

        ArgumentCaptor<CompletedSurveyEntity> saved = ArgumentCaptor.forClass(CompletedSurveyEntity.class);
        verify(responseRepository).save(saved.capture());
        CompletedSurveyEntity completed = saved.getValue();

        assertThat(completed.getUser()).isSameAs(user);
        assertThat(completed.getSurvey()).isSameAs(survey);
        assertThat(completed.getToken().getUser()).isSameAs(user);
        assertThat(completed.getToken().getToken()).isEqualTo("tkn-1");
        assertThat(completed.getAnswers()).hasSize(2);
        assertThat(completed.getAnswers()).allSatisfy(a -> assertThat(a.getResponse()).isSameAs(completed));
        assertThat(completed.getAnswers().get(0).getLikertValue()).isEqualTo(5);
        assertThat(completed.getAnswers().get(1).getFreetextAnswer()).isEqualTo("Harika");
    }

    @Test
    void should_skip_answers_for_unknown_questions() {
        when(userRepository.findUserEntityByUsername("katilimci")).thenReturn(new UserEntity());
        when(questionRepository.findById(99L)).thenReturn(Optional.empty());

        voteService.submitVote(request("katilimci", answer(99L, 3, null)), new SurveyEntity());

        ArgumentCaptor<CompletedSurveyEntity> saved = ArgumentCaptor.forClass(CompletedSurveyEntity.class);
        verify(responseRepository).save(saved.capture());
        assertThat(saved.getValue().getAnswers()).isEmpty();
    }

    @Test
    void should_throw_when_user_does_not_exist() {
        when(userRepository.findUserEntityByUsername("silinmis")).thenReturn(null);

        assertThatThrownBy(() -> voteService.submitVote(request("silinmis"), new SurveyEntity()))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Oy veren kullanıcı bulunamadı.");
        verify(responseRepository, never()).save(any());
    }
}
