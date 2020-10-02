package kz.uco.tsadv.modules.learning.model.feedback;

import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.core.sys.AppContext;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

@Table(name = "TSADV_LEARNING_FEEDBACK_ANSWER", uniqueConstraints = {
    @UniqueConstraint(name = "IDX_TSADV_LEARNING_FEEDBACK_ANSWER_UNQ", columnNames = {"FEEDBACK_QUESTION_ID", "ANSWER_LANG_VALUE1"})
})
@Entity(name = "tsadv$LearningFeedbackAnswer")
public class LearningFeedbackAnswer extends AbstractParentEntity {
    private static final long serialVersionUID = 181371465465290528L;

    @NotNull
    @Column(name = "ORDER_", nullable = false)
    protected Integer order;

    @NotNull
    @Column(name = "SCORE", nullable = false)
    protected Integer score;

    @NotNull
    @Column(name = "ANSWER_LANG_VALUE1", nullable = false, length = 2000)
    protected String answerLangValue1;

    @Column(name = "ANSWER_LANG_VALUE2", length = 2000)
    protected String answerLangValue2;

    @Column(name = "ANSWER_LANG_VALUE3", length = 2000)
    protected String answerLangValue3;

    @Column(name = "ANSWER_LANG_VALUE4", length = 2000)
    protected String answerLangValue4;

    @Column(name = "ANSWER_LANG_VALUE5", length = 2000)
    protected String answerLangValue5;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "FEEDBACK_QUESTION_ID")
    protected LearningFeedbackQuestion feedbackQuestion;

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

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getScore() {
        return score;
    }

    public void setAnswerLangValue1(String answerLangValue1) {
        this.answerLangValue1 = answerLangValue1;
    }

    public String getAnswerLangValue1() {
        return answerLangValue1;
    }

    public void setAnswerLangValue2(String answerLangValue2) {
        this.answerLangValue2 = answerLangValue2;
    }

    public String getAnswerLangValue2() {
        return answerLangValue2;
    }

    public void setAnswerLangValue3(String answerLangValue3) {
        this.answerLangValue3 = answerLangValue3;
    }

    public String getAnswerLangValue3() {
        return answerLangValue3;
    }

    public void setAnswerLangValue4(String answerLangValue4) {
        this.answerLangValue4 = answerLangValue4;
    }

    public String getAnswerLangValue4() {
        return answerLangValue4;
    }

    public void setAnswerLangValue5(String answerLangValue5) {
        this.answerLangValue5 = answerLangValue5;
    }

    public String getAnswerLangValue5() {
        return answerLangValue5;
    }

    @MetaProperty(related = {"answerLangValue1", "answerLangValue2", "answerLangValue3", "answerLangValue4", "answerLangValue5"})
    public String getAnswerLangValue() {
        UserSessionSource userSessionSource = AppBeans.get("cuba_UserSessionSource");
        String language = userSessionSource.getLocale().getLanguage();
        String descriptionOrder = AppContext.getProperty("base.abstractDictionary.langOrder");

        if (descriptionOrder != null) {
            List<String> langs = Arrays.asList(descriptionOrder.split(";"));
            switch (langs.indexOf(language)) {
                case 0: {
                    return answerLangValue1;
                }
                case 1: {
                    return answerLangValue2;
                }
                case 2: {
                    return answerLangValue3;
                }
                case 3: {
                    return answerLangValue4;
                }
                case 4: {
                    return answerLangValue5;
                }
                default:
                    return answerLangValue1;
            }
        }
        return answerLangValue1;
    }


}