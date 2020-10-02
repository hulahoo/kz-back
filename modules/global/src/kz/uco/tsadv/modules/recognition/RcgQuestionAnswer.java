package kz.uco.tsadv.modules.recognition;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.FileDescriptor;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.core.sys.AppContext;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

@Table(name = "TSADV_RCG_QUESTION_ANSWER")
@Entity(name = "tsadv$RcgQuestionAnswer")
public class RcgQuestionAnswer extends StandardEntity {
    private static final long serialVersionUID = -3247840294790408716L;

    @Column(name = "TEXT_LANG1", length = 1000)
    protected String textLang1;

    @Column(name = "TEXT_LANG2", length = 1000)
    protected String textLang2;

    @Column(name = "TEXT_LANG3", length = 1000)
    protected String textLang3;

    @Column(name = "TEXT_LANG4", length = 1000)
    protected String textLang4;

    @Column(name = "TEXT_LANG5", length = 1000)
    protected String textLang5;

    @NotNull
    @Column(name = "SCORE", nullable = false)
    protected Integer score;

    @Column(name = "CODE")
    protected String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ICON_ID")
    protected FileDescriptor icon;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "RCG_QUESTION_ID")
    protected RcgQuestion rcgQuestion;

    public void setTextLang1(String textLang1) {
        this.textLang1 = textLang1;
    }

    public String getTextLang1() {
        return textLang1;
    }

    public void setTextLang2(String textLang2) {
        this.textLang2 = textLang2;
    }

    public String getTextLang2() {
        return textLang2;
    }

    public void setTextLang3(String textLang3) {
        this.textLang3 = textLang3;
    }

    public String getTextLang3() {
        return textLang3;
    }

    public void setTextLang4(String textLang4) {
        this.textLang4 = textLang4;
    }

    public String getTextLang4() {
        return textLang4;
    }

    public void setTextLang5(String textLang5) {
        this.textLang5 = textLang5;
    }

    public String getTextLang5() {
        return textLang5;
    }


    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getScore() {
        return score;
    }


    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }


    public void setRcgQuestion(RcgQuestion rcgQuestion) {
        this.rcgQuestion = rcgQuestion;
    }

    public RcgQuestion getRcgQuestion() {
        return rcgQuestion;
    }


    public void setIcon(FileDescriptor icon) {
        this.icon = icon;
    }

    public FileDescriptor getIcon() {
        return icon;
    }

    @MetaProperty(related = "textLang1,textLang2,textLang3,textLang4,textLang5")
    public String getText() {
        UserSessionSource userSessionSource = AppBeans.get("cuba_UserSessionSource");
        String language = userSessionSource.getLocale().getLanguage();
        String langOrder = AppContext.getProperty("base.abstractDictionary.langOrder");

        if (langOrder != null) {
            List<String> langs = Arrays.asList(langOrder.split(";"));
            switch (langs.indexOf(language)) {
                case 0: {
                    return textLang1;
                }
                case 1: {
                    return textLang2;
                }
                case 2: {
                    return textLang3;
                }
                case 3: {
                    return textLang4;
                }
                case 4: {
                    return textLang5;
                }
                default:
                    return textLang1;
            }
        }

        return textLang1;
    }

}