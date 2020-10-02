package kz.uco.tsadv.modules.recruitment.model;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

import javax.persistence.*;
import java.util.List;

@NamePattern("%s|id")
@Table(name = "TSADV_INTERVIEW_QUESTIONNAIRE")
@Entity(name = "tsadv$InterviewQuestionnaire")
public class InterviewQuestionnaire extends AbstractParentEntity {
    private static final long serialVersionUID = -4713329912212665034L;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "INTERVIEW_ID")
    protected Interview interview;

    @OnDeleteInverse(DeletePolicy.DENY)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "QUESTIONNAIRE_ID")
    protected RcQuestionnaire questionnaire;

    @OrderBy("order")
    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "interviewQuestionnaire")
    protected List<kz.uco.tsadv.modules.recruitment.model.InterviewQuestion> questions;

    @Transient
    @MetaProperty
    protected Double totalScore;

    @Transient
    @MetaProperty
    protected Double totalMaxScore;

    public void setTotalMaxScore(Double totalMaxScore) {
        this.totalMaxScore = totalMaxScore;
    }

    public Double getTotalMaxScore() {
        return totalMaxScore;
    }


    public void setTotalScore(Double totalScore) {
        this.totalScore = totalScore;
    }

    public Double getTotalScore() {
        return totalScore;
    }


    public void setQuestions(List<kz.uco.tsadv.modules.recruitment.model.InterviewQuestion> questions) {
        this.questions = questions;
    }

    public List<InterviewQuestion> getQuestions() {
        return questions;
    }


    public void setInterview(Interview interview) {
        this.interview = interview;
    }

    public Interview getInterview() {
        return interview;
    }

    public void setQuestionnaire(RcQuestionnaire questionnaire) {
        this.questionnaire = questionnaire;
    }

    public RcQuestionnaire getQuestionnaire() {
        return questionnaire;
    }


}