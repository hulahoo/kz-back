package kz.uco.tsadv.modules.recruitment.model;

import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.core.sys.AppContext;
import kz.uco.tsadv.modules.recruitment.enums.RcAnswerResult;
import kz.uco.tsadv.modules.recruitment.model.*;
import kz.uco.tsadv.modules.recruitment.model.RcQuestion;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;

@NamePattern("%s|answerText")
@Table(name = "TSADV_RC_ANSWER")
@Entity(name = "tsadv$RcAnswer")
public class RcAnswer extends AbstractParentEntity {
    private static final long serialVersionUID = -4824983547777337488L;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "QUESTION_ID")
    protected RcQuestion question;

    @Column(name = "ORDER_", nullable = false)
    protected Integer order;

    @Column(name = "ANSWER_TEXT1", nullable = false, length = 2000)
    protected String answerText1;

    @Column(name = "ANSWER_TEXT2", length = 2000)
    protected String answerText2;

    @Column(name = "ANSWER_TEXT3", length = 2000)
    protected String answerText3;

    @Column(name = "ANSWER_TEXT4", length = 2000)
    protected String answerText4;

    @Column(name = "ANSWER_TEXT5", length = 2000)
    protected String answerText5;

    @Column(name = "ANSWER_RESULT")
    protected String answerResult;

    @Column(name = "POSITIVE_", nullable = false)
    protected Boolean positive = false;
    public RcQuestion getQuestion() {
        return question;
    }

    public void setQuestion(RcQuestion question) {
        this.question = question;
    }



    public void setPositive(Boolean positive) {
        this.positive = positive;
    }

    public Boolean getPositive() {
        return positive;
    }


    public void setAnswerText1(String answerText1) {
        this.answerText1 = answerText1;
    }

    public String getAnswerText1() {
        return answerText1;
    }

    public void setAnswerText2(String answerText2) {
        this.answerText2 = answerText2;
    }

    public String getAnswerText2() {
        return answerText2;
    }

    public void setAnswerText3(String answerText3) {
        this.answerText3 = answerText3;
    }

    public String getAnswerText3() {
        return answerText3;
    }

    public void setAnswerText4(String answerText4) {
        this.answerText4 = answerText4;
    }

    public String getAnswerText4() {
        return answerText4;
    }

    public void setAnswerText5(String answerText5) {
        this.answerText5 = answerText5;
    }

    public String getAnswerText5() {
        return answerText5;
    }

    public void setAnswerResult(RcAnswerResult answerResult) {
        this.answerResult = answerResult == null ? null : answerResult.getId();
    }

    public RcAnswerResult getAnswerResult() {
        return answerResult == null ? null : RcAnswerResult.fromId(answerResult);
    }




    public void setOrder(Integer order) {
        this.order = order;
    }

    public Integer getOrder() {
        return order;
    }

    @MetaProperty(related = "answerText1,answerText2,answerText3,answerText4,answerText5")
    public String getAnswerText() {
        UserSessionSource userSessionSource = AppBeans.get("cuba_UserSessionSource");
        String language = userSessionSource.getLocale().getLanguage();
        String langOrder = AppContext.getProperty("tal.abstractDictionary.langOrder");

        if (langOrder != null) {
            List<String> langs = Arrays.asList(langOrder.split(";"));
            switch (langs.indexOf(language)) {
                case 0: {
                    return answerText1;
                }
                case 1: {
                    return answerText2;
                }
                case 2: {
                    return answerText3;
                }
                case 3: {
                    return answerText4;
                }
                case 4: {
                    return answerText5;
                }
                default:
                    return answerText1;
            }
        }
        return answerText1;
    }
}