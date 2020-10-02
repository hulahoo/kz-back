package kz.uco.tsadv.modules.learning.model;

import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.tsadv.modules.learning.model.*;
import kz.uco.tsadv.modules.learning.model.CourseSectionAttempt;
import kz.uco.tsadv.modules.learning.model.Question;
import kz.uco.tsadv.modules.learning.model.TestSection;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Table(name = "TSADV_PERSON_ANSWER")
@Entity(name = "tsadv$PersonAnswer")
public class PersonAnswer extends AbstractParentEntity {
    private static final long serialVersionUID = 7595059243243396327L;

    @OnDelete(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TEST_SECTION_ID")
    protected TestSection testSection;

    @NotNull
    @Column(name = "CORRECT", nullable = false)
    protected Boolean correct = false;

    @NotNull
    @Column(name = "ANSWERED", nullable = false)
    protected Boolean answered = false;

    @NotNull
    @Column(name = "SCORE", nullable = false)
    protected Integer score;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "QUESTION_ID")
    protected Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ATTEMPT_ID")
    protected CourseSectionAttempt attempt;

    @Lob
    @Column(name = "ANSWER")
    protected String answer;

    public void setAnswered(Boolean answered) {
        this.answered = answered;
    }

    public Boolean getAnswered() {
        return answered;
    }


    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }


    public void setCorrect(Boolean correct) {
        this.correct = correct;
    }

    public Boolean getCorrect() {
        return correct;
    }


    public TestSection getTestSection() {
        return testSection;
    }

    public void setTestSection(TestSection testSection) {
        this.testSection = testSection;
    }


    public void setQuestion(Question question) {
        this.question = question;
    }

    public Question getQuestion() {
        return question;
    }

    public void setAttempt(CourseSectionAttempt attempt) {
        this.attempt = attempt;
    }

    public CourseSectionAttempt getAttempt() {
        return attempt;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getAnswer() {
        return answer;
    }


}