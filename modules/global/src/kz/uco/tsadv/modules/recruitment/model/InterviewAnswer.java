package kz.uco.tsadv.modules.recruitment.model;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.tsadv.modules.recruitment.model.*;
import kz.uco.tsadv.modules.recruitment.model.InterviewQuestion;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

import javax.persistence.*;
import java.util.Date;

@NamePattern("%s|id")
@Table(name = "TSADV_INTERVIEW_ANSWER")
@Entity(name = "tsadv$InterviewAnswer")
public class InterviewAnswer extends AbstractParentEntity {
    private static final long serialVersionUID = 498625313409043936L;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "INTERVIEW_QUESTION_ID")
    protected kz.uco.tsadv.modules.recruitment.model.InterviewQuestion interviewQuestion;

    @Column(name = "WEIGHT")
    protected Double weight;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ANSWER_ID")
    protected RcAnswer answer;

    @Column(name = "TEXT_ANSWER", length = 4000)
    protected String textAnswer;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_ANSWER")
    protected Date dateAnswer;

    @Column(name = "BOOLEAN_ANSWER")
    protected Boolean booleanAnswer;

    @Column(name = "ORDER_")
    protected Integer order;

    @Column(name = "NUMBER_ANSWER")
    protected Double numberAnswer;


    public void setNumberAnswer(Double numberAnswer) {
        this.numberAnswer = numberAnswer;
    }

    public Double getNumberAnswer() {
        return numberAnswer;
    }


    public void setOrder(Integer order) {
        this.order = order;
    }

    public Integer getOrder() {
        return order;
    }


    public void setBooleanAnswer(Boolean booleanAnswer) {
        this.booleanAnswer = booleanAnswer;
    }

    public Boolean getBooleanAnswer() {
        return booleanAnswer;
    }


    public void setTextAnswer(String textAnswer) {
        this.textAnswer = textAnswer;
    }

    public String getTextAnswer() {
        return textAnswer;
    }

    public void setDateAnswer(Date dateAnswer) {
        this.dateAnswer = dateAnswer;
    }

    public Date getDateAnswer() {
        return dateAnswer;
    }


    public void setInterviewQuestion(kz.uco.tsadv.modules.recruitment.model.InterviewQuestion interviewQuestion) {
        this.interviewQuestion = interviewQuestion;
    }

    public InterviewQuestion getInterviewQuestion() {
        return interviewQuestion;
    }

    public void setAnswer(RcAnswer answer) {
        this.answer = answer;
    }

    public RcAnswer getAnswer() {
        return answer;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getWeight() {
        return weight;
    }


}