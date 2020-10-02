package kz.uco.tsadv.modules.performance.model;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.core.sys.AppContext;
import kz.uco.tsadv.modules.performance.model.*;
import kz.uco.tsadv.modules.performance.model.Questionnaire;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.performance.dictionary.DicQuestionType;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;
import kz.uco.tsadv.modules.learning.enums.QuestionType;
import kz.uco.tsadv.modules.performance.dictionary.DicQuestionnaireQuestionSection;

@NamePattern("%s|questionText")
@Table(name = "TSADV_QUESTIONNAIRE_QUESTION")
@Entity(name = "tsadv$QuestionnaireQuestion")
public class QuestionnaireQuestion extends AbstractParentEntity {
    private static final long serialVersionUID = -6825926870999125464L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "QUESTIONNAIRE_ID")
    protected Questionnaire questionnaire;

    @Column(name = "ORDER_")
    protected Integer order;

    @OnDelete(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SECTION_ID")
    protected DicQuestionnaireQuestionSection section;

    @Column(name = "QUESTION_TEXT_LANG1", length = 2000)
    protected String questionTextLang1;

    @Column(name = "QUESTION_TEXT_LANG2", length = 2000)
    protected String questionTextLang2;

    @Column(name = "QUESTION_TEXT_LANG3", length = 2000)
    protected String questionTextLang3;

    @Column(name = "QUESTION_TEXT_LANG4", length = 2000)
    protected String questionTextLang4;

    @Column(name = "QUESTION_TEXT_LANG5", length = 2000)
    protected String questionTextLang5;

    @Transient
    @MetaProperty(related = {"questionTextLang1", "questionTextLang2", "questionTextLang3", "questionTextLang4", "questionTextLang5"})
    protected String questionText;

    @Column(name = "QUESTION_TYPE", nullable = false)
    protected Integer questionType;

    @Column(name = "SCORE")
    protected Integer score;


    @OrderBy("order")
    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "question")
    protected List<QuestionAnswer> answer;
    public void setOrder(Integer order) {
        this.order = order;
    }

    public Integer getOrder() {
        return order;
    }


    public void setSection(DicQuestionnaireQuestionSection section) {
        this.section = section;
    }

    public DicQuestionnaireQuestionSection getSection() {
        return section;
    }


    public QuestionType getQuestionType() {
        return questionType == null ? null : QuestionType.fromId(questionType);
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType == null ? null : questionType.getId();
    }


    public void setQuestionTextLang1(String questionTextLang1) {
        this.questionTextLang1 = questionTextLang1;
    }

    public String getQuestionTextLang1() {
        return questionTextLang1;
    }

    public void setQuestionTextLang2(String questionTextLang2) {
        this.questionTextLang2 = questionTextLang2;
    }

    public String getQuestionTextLang2() {
        return questionTextLang2;
    }

    public void setQuestionTextLang3(String questionTextLang3) {
        this.questionTextLang3 = questionTextLang3;
    }

    public String getQuestionTextLang3() {
        return questionTextLang3;
    }

    public void setQuestionTextLang4(String questionTextLang4) {
        this.questionTextLang4 = questionTextLang4;
    }

    public String getQuestionTextLang4() {
        return questionTextLang4;
    }

    public void setQuestionTextLang5(String questionTextLang5) {
        this.questionTextLang5 = questionTextLang5;
    }

    public String getQuestionTextLang5() {
        return questionTextLang5;
    }


    public Questionnaire getQuestionnaire() {
        return questionnaire;
    }

    public void setQuestionnaire(Questionnaire questionnaire) {
        this.questionnaire = questionnaire;
    }



    public void setAnswer(List<QuestionAnswer> answer) {
        this.answer = answer;
    }

    public List<QuestionAnswer> getAnswer() {
        return answer;
    }




    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getQuestionText() {
        UserSessionSource userSessionSource = AppBeans.get("cuba_UserSessionSource");
        String language = userSessionSource.getLocale().getLanguage();
        String questionTextOrder = AppContext.getProperty("base.abstractDictionary.langOrder");
        if (questionTextOrder != null){
            List<String> langs = Arrays.asList(questionTextOrder.split(";"));
            switch (langs.indexOf(language)){
                case 0:
                    return questionTextLang1;
                case 1:
                    return questionTextLang2;
                case 2:
                    return questionTextLang3;
                case 3:
                    return questionTextLang4;
                case 4:
                    return questionTextLang5;
                default:
                    return questionTextLang1;
            }
        }
        return questionText;
    }



    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getScore() {
        return score;
    }


}