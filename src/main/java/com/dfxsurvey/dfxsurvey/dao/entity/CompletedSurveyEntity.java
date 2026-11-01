package com.dfxsurvey.dfxsurvey.dao.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "responses")
public class CompletedSurveyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id")
    private SurveyEntity survey;

    // Token oy ile birlikte oluşturulur ve ayrı bir repository'si yok; oy kaydedilince o da kaydedilsin
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "token_id")
    private TokenEntity token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    private LocalDateTime submittedAt;

    @PrePersist
    public void prePersist() {
        if (submittedAt == null) {
            submittedAt = LocalDateTime.now();
        }
    }

    // AnswerEntity.response bu nesneyi gösterdiği için @Data'nın toString/hashCode'u sonsuz döngüye girmesin
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "response", cascade = CascadeType.ALL)
    private List<AnswerEntity> answers;
}
