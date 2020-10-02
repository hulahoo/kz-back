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
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.performance.dictionary.DicQuestionnaireStatus;
import kz.uco.tsadv.modules.recognition.dictionary.DicQuestionnaireType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@NamePattern("%s|questionnaireNameLang1")
@Table(name = "TSADV_QUESTIONNAIRE")
@Entity(name = "tsadv$Questionnaire")
public class Questionnaire extends AbstractParentEntity {
    private static final long serialVersionUID = -947452432081105411L;

    @Transient
    @MetaProperty(related = {"descriptionLang1", "descriptionNameLang2", "descriptionNameLang3", "descriptionNameLang4", "descriptionNameLang5"})
    protected String description;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "questionnaire")
    protected List<QuestionnaireResultScale> scale;

    @Column(name = "DESCRIPTION_LANG1", length = 4000)
    protected String descriptionLang1;

    @Column(name = "DESCRIPTION_LANG3", length = 4000)
    protected String descriptionLang3;

    @Column(name = "DESCRIPTION_LANG4", length = 4000)
    protected String descriptionLang4;

    @Column(name = "DESCRIPTION_LANG5", length = 4000)
    protected String descriptionLang5;

    @Column(name = "DESCRIPTION_LANG2", length = 4000)
    protected String descriptionLang2;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "QUESTIONNAIRE_TYPE_ID")
    protected DicQuestionnaireType questionnaireType;

    @Lookup(type = LookupType.DROPDOWN)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "STATUS_ID")
    protected DicQuestionnaireStatus status;


    @OrderBy("order")
    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "questionnaire")
    protected List<QuestionnaireQuestion> question;

    @Temporal(TemporalType.DATE)
    @Column(name = "START_DATE", nullable = false)
    protected Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "END_DATE", nullable = false)
    protected Date endDate;

    @Transient
    @MetaProperty(related = {"questionnaireNameLang1", "questionnaireNameLang2", "questionnaireNameLang3", "questionnaireNameLang4", "questionnaireNameLang5"})
    protected String questionnaireName;

    @NotNull
    @Column(name = "QUESTIONNAIRE_NAME_LANG1", nullable = false)
    protected String questionnaireNameLang1;

    @Column(name = "QUESTIONNAIRE_NAME_LANG2")
    protected String questionnaireNameLang2;

    @Column(name = "QUESTIONNAIRE_NAME_LANG3")
    protected String questionnaireNameLang3;

    @Column(name = "QUESTIONNAIRE_NAME_LANG4")
    protected String questionnaireNameLang4;

    @Column(name = "QUESTIONNAIRE_NAME_LANG5")
    protected String questionnaireNameLang5;

    public void setScale(List<QuestionnaireResultScale> scale) {
        this.scale = scale;
    }

    public List<QuestionnaireResultScale> getScale() {
        return scale;
    }


    public void setQuestionnaireNameLang1(String questionnaireNameLang1) {
        this.questionnaireNameLang1 = questionnaireNameLang1;
    }

    public String getQuestionnaireNameLang1() {
        return questionnaireNameLang1;
    }

    public void setQuestionnaireNameLang2(String questionnaireNameLang2) {
        this.questionnaireNameLang2 = questionnaireNameLang2;
    }

    public String getQuestionnaireNameLang2() {
        return questionnaireNameLang2;
    }

    public void setQuestionnaireNameLang3(String questionnaireNameLang3) {
        this.questionnaireNameLang3 = questionnaireNameLang3;
    }

    public String getQuestionnaireNameLang3() {
        return questionnaireNameLang3;
    }

    public void setQuestionnaireNameLang4(String questionnaireNameLang4) {
        this.questionnaireNameLang4 = questionnaireNameLang4;
    }

    public String getQuestionnaireNameLang4() {
        return questionnaireNameLang4;
    }

    public void setQuestionnaireNameLang5(String questionnaireNameLang5) {
        this.questionnaireNameLang5 = questionnaireNameLang5;
    }

    public String getQuestionnaireNameLang5() {
        return questionnaireNameLang5;
    }

    public String getDescription() {
        UserSessionSource userSessionSource = AppBeans.get("cuba_UserSessionSource");
        String language = userSessionSource.getLocale().getLanguage();
        String descriptionOrder = AppContext.getProperty("base.abstractDictionary.langOrder");
        if (descriptionOrder != null){
            List<String> langs = Arrays.asList(descriptionOrder.split(";"));
            switch (langs.indexOf(language)){
                case 0:
                    return descriptionLang1;
                case 1:
                    return descriptionLang2;
                case 2:
                    return descriptionLang3;
                case 3:
                    return descriptionLang4;
                case 4:
                    return descriptionLang5;
                default:
                    return descriptionLang1;
            }
        }
        return descriptionLang1;
    }

    public String getQuestionnaireName() {
        UserSessionSource userSessionSource = AppBeans.get("cuba_UserSessionSource");
        String language = userSessionSource.getLocale().getLanguage();
        String questionnaireNameOrder = AppContext.getProperty("base.abstractDictionary.langOrder");
        if (questionnaireNameOrder != null){
            List<String> langs = Arrays.asList(questionnaireNameOrder.split(";"));
            switch (langs.indexOf(language)){
                case 0:
                    return questionnaireNameLang1;
                case 1:
                    return questionnaireNameLang2;
                case 2:
                    return questionnaireNameLang3;
                case 3:
                    return questionnaireNameLang4;
                case 4:
                    return questionnaireNameLang5;
                default:
                    return questionnaireNameLang1;
            }
        }
        return questionnaireNameLang1;
    }

    public void setDescriptionLang1(String descriptionLang1) {
        this.descriptionLang1 = descriptionLang1;
    }

    public String getDescriptionLang1() {
        return descriptionLang1;
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

    public void setDescriptionLang2(String descriptionLang2) {
        this.descriptionLang2 = descriptionLang2;
    }

    public String getDescriptionLang2() {
        return descriptionLang2;
    }



    public void setQuestionnaireType(DicQuestionnaireType questionnaireType) {
        this.questionnaireType = questionnaireType;
    }

    public DicQuestionnaireType getQuestionnaireType() {
        return questionnaireType;
    }





    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getEndDate() {
        return endDate;
    }


    public void setQuestion(List<QuestionnaireQuestion> question) {
        this.question = question;
    }

    public List<QuestionnaireQuestion> getQuestion() {
        return question;
    }


    public DicQuestionnaireStatus getStatus() {
        return status;
    }

    public void setStatus(DicQuestionnaireStatus status) {
        this.status = status;
    }





}