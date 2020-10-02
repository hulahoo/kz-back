package kz.uco.tsadv.modules.personal.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import kz.uco.tsadv.modules.performance.model.QuestionAnswer;
import kz.uco.tsadv.modules.performance.model.QuestionnaireQuestion;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

@Table(name = "TSADV_PERSON_QUESTIONNAIRE_ANSWER")
@Entity(name = "tsadv$PersonQuestionnaireAnswer")
public class PersonQuestionnaireAnswer extends AbstractParentEntity {
    private static final long serialVersionUID = -7731400027731939798L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSON_QUESTIONNAIRE_ID")
    protected PersonQuestionnaire personQuestionnaire;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "QUESTION_ID")
    protected QuestionnaireQuestion question;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ANSWER_ID")
    protected QuestionAnswer answer;

    @Column(name = "SCORE")
    protected Integer score;

    @Column(name = "TEXT_ANSWER", length = 2000)
    protected String textAnswer;

    public void setPersonQuestionnaire(PersonQuestionnaire personQuestionnaire) {
        this.personQuestionnaire = personQuestionnaire;
    }

    public PersonQuestionnaire getPersonQuestionnaire() {
        return personQuestionnaire;
    }

    public void setQuestion(QuestionnaireQuestion question) {
        this.question = question;
    }

    public QuestionnaireQuestion getQuestion() {
        return question;
    }

    public void setAnswer(QuestionAnswer answer) {
        this.answer = answer;
    }

    public QuestionAnswer getAnswer() {
        return answer;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getScore() {
        return score;
    }

    public void setTextAnswer(String textAnswer) {
        this.textAnswer = textAnswer;
    }

    public String getTextAnswer() {
        return textAnswer;
    }


}