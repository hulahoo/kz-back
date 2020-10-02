package kz.uco.tsadv.modules.learning.model.feedback;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

@Table(name = "TSADV_LEARNING_FEEDBACK_TEMPLATE_QUESTION")
@Entity(name = "tsadv$LearningFeedbackTemplateQuestion")
public class LearningFeedbackTemplateQuestion extends AbstractParentEntity {
    private static final long serialVersionUID = -2857085101544185242L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "FEEDBACK_TEMPLATE_ID")
    protected LearningFeedbackTemplate feedbackTemplate;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "FEEDBACK_QUESTION_ID")
    protected LearningFeedbackQuestion feedbackQuestion;

    @NotNull
    @Column(name = "ORDER_", nullable = false)
    protected Integer order;

    public void setFeedbackTemplate(LearningFeedbackTemplate feedbackTemplate) {
        this.feedbackTemplate = feedbackTemplate;
    }

    public LearningFeedbackTemplate getFeedbackTemplate() {
        return feedbackTemplate;
    }

    public void setFeedbackQuestion(LearningFeedbackQuestion feedbackQuestion) {
        this.feedbackQuestion = feedbackQuestion;
    }

    public LearningFeedbackQuestion getFeedbackQuestion() {
        return feedbackQuestion;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Integer getOrder() {
        return order;
    }


}