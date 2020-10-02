package kz.uco.tsadv.modules.recruitment.model;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.recruitment.enums.RcAnswerType;

import javax.persistence.*;
import java.util.List;

@NamePattern("%s|id")
@Table(name = "TSADV_INTERVIEW_QUESTION")
@Entity(name = "tsadv$InterviewQuestion")
public class InterviewQuestion extends AbstractParentEntity {
    private static final long serialVersionUID = -2750232747371041861L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "INTERVIEW_QUESTIONNAIRE_ID")
    protected InterviewQuestionnaire interviewQuestionnaire;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "QUESTION_ID")
    protected RcQuestion question;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "interviewQuestion")
    protected List<InterviewAnswer> answers;

    @Column(name = "ORDER_")
    protected Integer order;

    @MetaProperty(related = {"answers", "question"})
    @Transient
    protected Double score;

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Integer getOrder() {
        return order;
    }

    public void setAnswers(List<InterviewAnswer> answers) {
        this.answers = answers;
    }

    public List<InterviewAnswer> getAnswers() {
        return answers;
    }

    public void setInterviewQuestionnaire(InterviewQuestionnaire interviewQuestionnaire) {
        this.interviewQuestionnaire = interviewQuestionnaire;
    }

    public InterviewQuestionnaire getInterviewQuestionnaire() {
        return interviewQuestionnaire;
    }

    public void setQuestion(RcQuestion question) {
        this.question = question;
    }

    public RcQuestion getQuestion() {
        return question;
    }

    public Double getScore() {
        if (this.question == null || this.question.getAnswerType() == null || this.question.getAnswerType() == RcAnswerType.DATE || this.question.getAnswerType() == RcAnswerType.TEXT || this.question.getAnswerType() == RcAnswerType.NUMBER)
            return null;

        if (this.getAnswers() == null) return null;

        this.score = 0d;
        this.getAnswers().forEach(answer -> this.score += (answer != null && answer.getDeleteTs() == null && answer.getBooleanAnswer() != null && answer.getBooleanAnswer() && answer.getWeight() != null ? answer.getWeight() : 0));
        return this.score;
    }

    @MetaProperty
    public InterviewQuestion getWholeObject() {
        return this;
    }
}