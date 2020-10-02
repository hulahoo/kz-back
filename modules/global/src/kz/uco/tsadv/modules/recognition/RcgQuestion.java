package kz.uco.tsadv.modules.recognition;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.core.sys.AppContext;
import kz.uco.tsadv.modules.recognition.enums.RcgAnswerType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;
import javax.persistence.OrderBy;

@Table(name = "TSADV_RCG_QUESTION")
@Entity(name = "tsadv$RcgQuestion")
public class RcgQuestion extends StandardEntity {
    private static final long serialVersionUID = -3691218350082167291L;

    @NotNull
    @Column(name = "TEXT_LANG1", nullable = false, length = 1000)
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
    @Column(name = "DESCRIPTION_LANG1", nullable = false, length = 1000)
    protected String descriptionLang1;

    @Column(name = "DESCRIPTION_LANG2", length = 1000)
    protected String descriptionLang2;

    @Column(name = "DESCRIPTION_LANG3", length = 1000)
    protected String descriptionLang3;

    @Column(name = "DESCRIPTION_LANG4", length = 1000)
    protected String descriptionLang4;

    @Column(name = "DESCRIPTION_LANG5", length = 1000)
    protected String descriptionLang5;

    @NotNull
    @Column(name = "ACTIVE", nullable = false)
    protected Boolean active = false;

    @NotNull
    @Column(name = "ANSWER_TYPE", nullable = false)
    protected String answerType;

    @Column(name = "COINS")
    protected Long coins;

    @OrderBy("score ASC")
    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "rcgQuestion")
    protected List<RcgQuestionAnswer> answers;


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

    public void setDescriptionLang1(String descriptionLang1) {
        this.descriptionLang1 = descriptionLang1;
    }

    public String getDescriptionLang1() {
        return descriptionLang1;
    }

    public void setDescriptionLang2(String descriptionLang2) {
        this.descriptionLang2 = descriptionLang2;
    }

    public String getDescriptionLang2() {
        return descriptionLang2;
    }

    public void setDescriptionLang3(String descriptionLang3) {
        this.descriptionLang3 = descriptionLang3;
    }

    public String getDescriptionLang3() {
        return descriptionLang3;
    }

    public void setDescriptionLang4(String descriptionLang4) {
        this.descriptionLang4 = descriptionLang4;
    }

    public String getDescriptionLang4() {
        return descriptionLang4;
    }

    public void setDescriptionLang5(String descriptionLang5) {
        this.descriptionLang5 = descriptionLang5;
    }

    public String getDescriptionLang5() {
        return descriptionLang5;
    }


    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getActive() {
        return active;
    }


    public void setAnswerType(RcgAnswerType answerType) {
        this.answerType = answerType == null ? null : answerType.getId();
    }

    public RcgAnswerType getAnswerType() {
        return answerType == null ? null : RcgAnswerType.fromId(answerType);
    }


    public void setCoins(Long coins) {
        this.coins = coins;
    }

    public Long getCoins() {
        return coins;
    }

    public void setAnswers(List<RcgQuestionAnswer> answers) {
        this.answers = answers;
    }

    public List<RcgQuestionAnswer> getAnswers() {
        return answers;
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

    @MetaProperty(related = "descriptionLang1,descriptionLang2,descriptionLang3,descriptionLang4,descriptionLang5")
    public String getDescription() {
        UserSessionSource userSessionSource = AppBeans.get("cuba_UserSessionSource");
        String language = userSessionSource.getLocale().getLanguage();
        String langOrder = AppContext.getProperty("base.abstractDictionary.langOrder");

        if (langOrder != null) {
            List<String> langs = Arrays.asList(langOrder.split(";"));
            switch (langs.indexOf(language)) {
                case 0: {
                    return descriptionLang1;
                }
                case 1: {
                    return descriptionLang2;
                }
                case 2: {
                    return descriptionLang3;
                }
                case 3: {
                    return descriptionLang4;
                }
                case 4: {
                    return descriptionLang5;
                }
                default:
                    return descriptionLang1;
            }
        }

        return descriptionLang1;
    }

}