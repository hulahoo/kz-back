package kz.uco.tsadv.modules.learning.model.feedback;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.core.sys.AppContext;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.learning.dictionary.DicLearningFeedbackQuestionType;
import kz.uco.tsadv.modules.learning.enums.feedback.LearningFeedbackQuestionType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

@Table(name = "TSADV_LEARNING_FEEDBACK_QUESTION")
@Entity(name = "tsadv$LearningFeedbackQuestion")
@NamePattern("%s|questionLangValue")
public class LearningFeedbackQuestion extends AbstractParentEntity {
    private static final long serialVersionUID = -2813532923570678196L;

    @NotNull
    @Column(name = "QUESTION_LANG_VALUE1", nullable = false, length = 2000)
    protected String questionLangValue1;

    @OrderBy("order")
    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "feedbackQuestion")
    protected List<LearningFeedbackAnswer> answers;

    @Column(name = "QUESTION_LANG_VALUE2", length = 2000)
    protected String questionLangValue2;

    @Column(name = "QUESTION_LANG_VALUE3", length = 2000)
    protected String questionLangValue3;

    @Column(name = "QUESTION_LANG_VALUE4", length = 2000)
    protected String questionLangValue4;

    @Column(name = "QUESTION_LANG_VALUE5", length = 2000)
    protected String questionLangValue5;

    @NotNull
    @Column(name = "QUESTION_TYPE", nullable = false)
    protected String questionType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DIC_QUESTION_TYPE_ID")
    protected DicLearningFeedbackQuestionType dicQuestionType;

    public void setDicQuestionType(DicLearningFeedbackQuestionType dicQuestionType) {
        this.dicQuestionType = dicQuestionType;
    }

    public DicLearningFeedbackQuestionType getDicQuestionType() {
        return dicQuestionType;
    }


    public void setAnswers(List<LearningFeedbackAnswer> answers) {
        this.answers = answers;
    }

    public List<LearningFeedbackAnswer> getAnswers() {
        return answers;
    }


    public void setQuestionLangValue1(String questionLangValue1) {
        this.questionLangValue1 = questionLangValue1;
    }

    public String getQuestionLangValue1() {
        return questionLangValue1;
    }

    public void setQuestionLangValue2(String questionLangValue2) {
        this.questionLangValue2 = questionLangValue2;
    }

    public String getQuestionLangValue2() {
        return questionLangValue2;
    }

    public void setQuestionLangValue3(String questionLangValue3) {
        this.questionLangValue3 = questionLangValue3;
    }

    public String getQuestionLangValue3() {
        return questionLangValue3;
    }

    public void setQuestionLangValue4(String questionLangValue4) {
        this.questionLangValue4 = questionLangValue4;
    }

    public String getQuestionLangValue4() {
        return questionLangValue4;
    }

    public void setQuestionLangValue5(String questionLangValue5) {
        this.questionLangValue5 = questionLangValue5;
    }

    public String getQuestionLangValue5() {
        return questionLangValue5;
    }

    public void setQuestionType(LearningFeedbackQuestionType questionType) {
        this.questionType = questionType == null ? null : questionType.getId();
    }

    public LearningFeedbackQuestionType getQuestionType() {
        return questionType == null ? null : LearningFeedbackQuestionType.fromId(questionType);
    }

    @MetaProperty(related = {"questionLangValue1", "questionLangValue2", "questionLangValue3", "questionLangValue4", "questionLangValue5"})
    public String getQuestionLangValue() {
        UserSessionSource userSessionSource = AppBeans.get("cuba_UserSessionSource");
        String language = userSessionSource.getLocale().getLanguage();
        String descriptionOrder = AppContext.getProperty("base.abstractDictionary.langOrder");

        if (descriptionOrder != null) {
            List<String> langs = Arrays.asList(descriptionOrder.split(";"));
            switch (langs.indexOf(language)) {
                case 0: {
                    return questionLangValue1;
                }
                case 1: {
                    return questionLangValue2;
                }
                case 2: {
                    return questionLangValue3;
                }
                case 3: {
                    return questionLangValue4;
                }
                case 4: {
                    return questionLangValue5;
                }
                default:
                    return questionLangValue1;
            }
        }
        return questionLangValue1;
    }


}