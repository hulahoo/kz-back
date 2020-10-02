package kz.uco.tsadv.modules.recruitment.model;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.tsadv.modules.recruitment.model.*;
import kz.uco.tsadv.modules.recruitment.model.RcQuestion;
import kz.uco.tsadv.modules.recruitment.model.RcQuestionnaire;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

import javax.persistence.*;
import java.util.List;

@NamePattern("%s|id")
@Table(name = "TSADV_RC_QUESTIONNAIRE_QUESTION")
@Entity(name = "tsadv$RcQuestionnaireQuestion")
public class RcQuestionnaireQuestion extends AbstractParentEntity {
    private static final long serialVersionUID = -6297351559733263328L;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "QUESTIONNAIRE_ID")
    protected kz.uco.tsadv.modules.recruitment.model.RcQuestionnaire questionnaire;

    @OnDeleteInverse(DeletePolicy.DENY)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "QUESTION_ID")
    protected RcQuestion question;

    @OrderBy("order")
    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "questionnaireQuestion")
    protected List<RcQuestionnaireAnswer> answers;

    @Column(name = "ORDER_")
    protected Integer order;

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Integer getOrder() {
        return order;
    }


    public void setAnswers(List<RcQuestionnaireAnswer> answers) {
        this.answers = answers;
    }

    public List<RcQuestionnaireAnswer> getAnswers() {
        return answers;
    }


    public void setQuestionnaire(kz.uco.tsadv.modules.recruitment.model.RcQuestionnaire questionnaire) {
        this.questionnaire = questionnaire;
    }

    public RcQuestionnaire getQuestionnaire() {
        return questionnaire;
    }

    public void setQuestion(RcQuestion question) {
        this.question = question;
    }

    public RcQuestion getQuestion() {
        return question;
    }


}