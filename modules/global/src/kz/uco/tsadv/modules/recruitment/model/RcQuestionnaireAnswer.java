package kz.uco.tsadv.modules.recruitment.model;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.tsadv.modules.recruitment.model.*;
import kz.uco.tsadv.modules.recruitment.model.RcAnswer;
import kz.uco.tsadv.modules.recruitment.model.RcQuestionnaireQuestion;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

import javax.persistence.*;

@NamePattern("%s|id")
@Table(name = "TSADV_RC_QUESTIONNAIRE_ANSWER")
@Entity(name = "tsadv$RcQuestionnaireAnswer")
public class RcQuestionnaireAnswer extends AbstractParentEntity {
    private static final long serialVersionUID = -267241255821953697L;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "QUESTIONNAIRE_QUESTION_ID")
    protected kz.uco.tsadv.modules.recruitment.model.RcQuestionnaireQuestion questionnaireQuestion;

    @OnDeleteInverse(DeletePolicy.DENY)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ANSWER_ID")
    protected RcAnswer answer;

    @Column(name = "WEIGHT")
    protected Double weight;

    @Column(name = "ORDER_")
    protected Integer order;

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Integer getOrder() {
        return order;
    }


    public void setQuestionnaireQuestion(kz.uco.tsadv.modules.recruitment.model.RcQuestionnaireQuestion questionnaireQuestion) {
        this.questionnaireQuestion = questionnaireQuestion;
    }

    public RcQuestionnaireQuestion getQuestionnaireQuestion() {
        return questionnaireQuestion;
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