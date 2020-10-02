package kz.uco.tsadv.modules.performance.model;

import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.core.sys.AppContext;
import kz.uco.tsadv.modules.performance.model.*;
import kz.uco.tsadv.modules.performance.model.QuestionnaireQuestion;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

import javax.persistence.*;
import com.haulmont.cuba.core.entity.FileDescriptor;

import java.util.Arrays;
import java.util.List;

@NamePattern("%s|answerLang1")
@Table(name = "TSADV_QUESTION_ANSWER")
@Entity(name = "tsadv$QuestionAnswer")
public class QuestionAnswer extends AbstractParentEntity {
    private static final long serialVersionUID = -7699450565778034456L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "QUESTION_ID")
    protected QuestionnaireQuestion question;

    @Column(name = "ORDER_")
    protected Integer order;

    @Column(name = "SCORE")
    protected Integer score;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ICON_ID")
    protected FileDescriptor icon;

    @Transient
    @MetaProperty(related = {"answerLang1", "answerLang2", "answerLang3", "answerLang4", "answerLang5"})
    protected String answer;

    @Column(name = "ANSWER_LANG1", length = 2000, nullable = false)
    protected String answerLang1;

    @Column(name = "ANSWER_LANG2", length = 2000)
    protected String answerLang2;

    @Column(name = "ANSWER_LANG3", length = 2000)
    protected String answerLang3;

    @Column(name = "ANSWER_LANG4", length = 2000)
    protected String answerLang4;

    @Column(name = "ANSWER_LANG5", length = 2000)
    protected String answerLang5;

    @Column(name = "CORRECT_ANSWER", nullable = false)
    protected Boolean correctAnswer = false;
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


    public void setAnswerLang1(String answerLang1) {
        this.answerLang1 = answerLang1;
    }

    public String getAnswerLang1() {
        return answerLang1;
    }

    public void setAnswerLang2(String answerLang2) {
        this.answerLang2 = answerLang2;
    }

    public String getAnswerLang2() {
        return answerLang2;
    }

    public void setAnswerLang3(String answerLang3) {
        this.answerLang3 = answerLang3;
    }

    public String getAnswerLang3() {
        return answerLang3;
    }

    public void setAnswerLang4(String answerLang4) {
        this.answerLang4 = answerLang4;
    }

    public String getAnswerLang4() {
        return answerLang4;
    }

    public void setAnswerLang5(String answerLang5) {
        this.answerLang5 = answerLang5;
    }

    public String getAnswerLang5() {
        return answerLang5;
    }

    public String getAnswer() {
        UserSessionSource userSessionSource = AppBeans.get("cuba_UserSessionSource");
        String language = userSessionSource.getLocale().getLanguage();
        String answerOrder = AppContext.getProperty("base.abstractDictionary.langOrder");
        if (answerOrder != null){
            List<String> langs = Arrays.asList(answerOrder.split(";"));
            switch (langs.indexOf(language)){
                case 0:
                    return answerLang1;
                case 1:
                    return answerLang2;
                case 2:
                    return answerLang3;
                case 3:
                    return answerLang4;
                case 4:
                    return answerLang5;
                default:
                    return answerLang1;
            }
        }
        return answer;
    }


    public void setIcon(FileDescriptor icon) {
        this.icon = icon;
    }

    public FileDescriptor getIcon() {
        return icon;
    }


    public QuestionnaireQuestion getQuestion() {
        return question;
    }

    public void setQuestion(QuestionnaireQuestion question) {
        this.question = question;
    }








    public void setCorrectAnswer(Boolean correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public Boolean getCorrectAnswer() {
        return correctAnswer;
    }


}