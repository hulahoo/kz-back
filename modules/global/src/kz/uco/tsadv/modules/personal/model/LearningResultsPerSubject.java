package kz.uco.tsadv.modules.personal.model;

import kz.uco.base.entity.abstraction.AbstractParentEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Table(name = "TSADV_LEARNING_RESULTS_PER_SUBJECT")
@Entity(name = "tsadv_LearningResultsPerSubject")
public class LearningResultsPerSubject extends AbstractParentEntity {
    private static final long serialVersionUID = 3199742697245902961L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "LEARNING_RESULT_ID")
    protected LearningResults learningResult;

    @NotNull
    @Column(name = "SUBJECT", nullable = false)
    protected String subject;

    @NotNull
    @Column(name = "SCORE", nullable = false)
    protected Double score;

    public void setScore(Double score) {
        this.score = score;
    }

    public Double getScore() {
        return score;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public LearningResults getLearningResult() {
        return learningResult;
    }

    public void setLearningResult(LearningResults learningResult) {
        this.learningResult = learningResult;
    }
}