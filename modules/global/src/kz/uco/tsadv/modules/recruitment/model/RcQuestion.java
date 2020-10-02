package kz.uco.tsadv.modules.recruitment.model;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.core.sys.AppContext;
import kz.uco.tsadv.modules.recruitment.enums.RcAnswerType;
import kz.uco.tsadv.modules.recruitment.enums.RcQuestionType;
import kz.uco.tsadv.modules.recruitment.model.RcAnswer;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.recruitment.dictionary.DicRcQuestionAccessibility;
import kz.uco.tsadv.modules.recruitment.dictionary.DicRcQuestionCategory;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;

@NamePattern("%s|id")
@Table(name = "TSADV_RC_QUESTION")
@Entity(name = "tsadv$RcQuestion")
public class RcQuestion extends AbstractParentEntity {
    private static final long serialVersionUID = 613150437079520293L;

    @Column(name = "QUESTION_TYPE", nullable = false)
    protected String questionType;

    @Column(name = "ANSWER_TYPE", nullable = false)
    protected String answerType;

    @Column(name = "QUESTION_TEXT1", nullable = false, length = 2000)
    protected String questionText1;

    @Column(name = "QUESTION_TEXT2", length = 2000)
    protected String questionText2;

    @Column(name = "QUESTION_TEXT3", length = 2000)
    protected String questionText3;

    @Column(name = "QUESTION_TEXT4", length = 2000)
    protected String questionText4;

    @Column(name = "QUESTION_TEXT5", length = 2000)
    protected String questionText5;

    @Column(name = "IS_ACTIVE", nullable = false)
    protected Boolean isActive = false;

    @OnDeleteInverse(DeletePolicy.DENY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "QUESTION_CATEGORY_ID")
    protected DicRcQuestionCategory questionCategory;

    @OnDeleteInverse(DeletePolicy.DENY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "QUESTION_ACCESSIBILITY_ID")
    protected DicRcQuestionAccessibility questionAccessibility;

    @OrderBy("order")
    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "question")
    protected List<RcAnswer> answers;

    public void setQuestionText1(String questionText1) {
        this.questionText1 = questionText1;
    }

    public String getQuestionText1() {
        return questionText1;
    }

    public void setQuestionText2(String questionText2) {
        this.questionText2 = questionText2;
    }

    public String getQuestionText2() {
        return questionText2;
    }

    public void setQuestionText3(String questionText3) {
        this.questionText3 = questionText3;
    }

    public String getQuestionText3() {
        return questionText3;
    }

    public void setQuestionText4(String questionText4) {
        this.questionText4 = questionText4;
    }

    public String getQuestionText4() {
        return questionText4;
    }

    public void setQuestionText5(String questionText5) {
        this.questionText5 = questionText5;
    }

    public String getQuestionText5() {
        return questionText5;
    }

    public void setAnswers(List<RcAnswer> answers) {
        this.answers = answers;
    }

    public List<RcAnswer> getAnswers() {
        return answers;
    }


    public void setQuestionAccessibility(DicRcQuestionAccessibility questionAccessibility) {
        this.questionAccessibility = questionAccessibility;
    }

    public DicRcQuestionAccessibility getQuestionAccessibility() {
        return questionAccessibility;
    }


    public void setQuestionCategory(DicRcQuestionCategory questionCategory) {
        this.questionCategory = questionCategory;
    }

    public DicRcQuestionCategory getQuestionCategory() {
        return questionCategory;
    }


    public void setAnswerType(RcAnswerType answerType) {
        this.answerType = answerType == null ? null : answerType.getId();
    }

    public RcAnswerType getAnswerType() {
        return answerType == null ? null : RcAnswerType.fromId(answerType);
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getIsActive() {
        return isActive;
    }


    public void setQuestionType(RcQuestionType questionType) {
        this.questionType = questionType == null ? null : questionType.getId();
    }

    public RcQuestionType getQuestionType() {
        return questionType == null ? null : RcQuestionType.fromId(questionType);
    }

    @MetaProperty(related = "questionText1,questionText2,questionText3,questionText4,questionText5")
    public String getQuestionText() {
        UserSessionSource userSessionSource = AppBeans.get("cuba_UserSessionSource");
        String language = userSessionSource.getLocale().getLanguage();
        String langOrder = AppContext.getProperty("tal.abstractDictionary.langOrder");

        if (langOrder != null) {
            List<String> langs = Arrays.asList(langOrder.split(";"));
            switch (langs.indexOf(language)) {
                case 0: {
                    return questionText1;
                }
                case 1: {
                    return questionText2;
                }
                case 2: {
                    return questionText3;
                }
                case 3: {
                    return questionText4;
                }
                case 4: {
                    return questionText5;
                }
                default:
                    return questionText1;
            }
        }
        return questionText1;
    }
}