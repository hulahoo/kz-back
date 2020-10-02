package kz.uco.tsadv.entity;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.core.sys.AppContext;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.validation.constraints.NotNull;

@NamePattern("%s|programNameLang")
@Table(name = "TSADV_TALENT_PROGRAM")
@Entity(name = "tsadv$TalentProgram")
public class TalentProgram extends StandardEntity {
    private static final long serialVersionUID = -3469820233680481803L;

    @Column(name = "PROGRAM_NAME_LANG_1")
    protected String programNameLang1;

    @Lob
    @Column(name = "ESSAY_REQUIREMENT_LANG1")
    protected String essayRequirementLang1;

    @Lob
    @Column(name = "ESSAY_REQUIREMENT_LANG2")
    protected String essayRequirementLang2;

    @Lob
    @Column(name = "ESSAY_REQUIREMENT_LANG3")
    protected String essayRequirementLang3;

    @Transient
    @MetaProperty(related = {"essayRequirementLang1", "essayRequirementLang2", "essayRequirementLang3"})
    protected String essayRequirementLang;

    @Transient
    @MetaProperty(related = {"programNameLang1", "programNameLang2", "programNameLang3"})
    protected String programNameLang;

    @Transient
    @MetaProperty(related = {"bannerLang1", "bannerLang2", "bannerLang3"})
    protected FileDescriptor bannerLang;

    @Transient
    @MetaProperty(related = {"participationRuleLang1", "participationRuleLang2", "participationRuleLang3"})
    protected String participationRuleLang;

    @Column(name = "PROGRAM_NAME_LANG_2")
    protected String programNameLang2;

    @Column(name = "PROGRAM_NAME_LANG_3")
    protected String programNameLang3;

    @Column(name = "IS_ACTIVE")
    protected Boolean isActive;

    @Temporal(TemporalType.DATE)
    @Column(name = "START_DATE")
    protected Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "END_DATE")
    protected Date endDate;

    @Lob
    @Column(name = "PARTICIPATION_RULE_LANG_1")
    protected String participationRuleLang1;

    @Lob
    @Column(name = "PARTICIPATION_RULE_LANG_2")
    protected String participationRuleLang2;

    @Lob
    @Column(name = "PARTICIPATION_RULE_LANG_3")
    protected String participationRuleLang3;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BANNER_LANG_1_ID")
    protected FileDescriptor bannerLang1;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BANNER_LANG_2_ID")
    protected FileDescriptor bannerLang2;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BANNER_LANG_3_ID")
    protected FileDescriptor bannerLang3;

    @Column(name = "WEB_LINK")
    protected String webLink;

    @NotNull
    @Column(name = "QUESTION_OF_ESSAY_RU", nullable = false)
    protected String questionOfEssayRu;

    @NotNull
    @Column(name = "QUESTION_OF_ESSAY_KZ", nullable = false)
    protected String questionOfEssayKz;

    @NotNull
    @Column(name = "QUESTION_OF_ESSAY_EN", nullable = false)
    protected String questionOfEssayEn;

    public void setQuestionOfEssayRu(String questionOfEssayRu) {
        this.questionOfEssayRu = questionOfEssayRu;
    }

    public String getQuestionOfEssayRu() {
        return questionOfEssayRu;
    }

    public void setQuestionOfEssayKz(String questionOfEssayKz) {
        this.questionOfEssayKz = questionOfEssayKz;
    }

    public String getQuestionOfEssayKz() {
        return questionOfEssayKz;
    }

    public void setQuestionOfEssayEn(String questionOfEssayEn) {
        this.questionOfEssayEn = questionOfEssayEn;
    }

    public String getQuestionOfEssayEn() {
        return questionOfEssayEn;
    }

    @MetaProperty(related = {"questionOfEssayRu", "questionOfEssayKz", "questionOfEssayEn"})
    public String getQuestionOfEssay() {
        UserSessionSource userSessionSource = AppBeans.get("cuba_UserSessionSource");
        String language = userSessionSource.getLocale().getLanguage();
        String langOrder = AppContext.getProperty("base.abstractDictionary.langOrder");

        if (langOrder != null) {
            List<String> langs = Arrays.asList(langOrder.split(";"));
            switch (langs.indexOf(language)) {
                case 0: {
                    return questionOfEssayRu;
                }
                case 1: {
                    return questionOfEssayKz;
                }
                case 2: {
                    return questionOfEssayEn;
                }
                default:
                    return questionOfEssayRu;
            }
        }

        return questionOfEssayRu;
    }


    public void setEssayRequirementLang1(String essayRequirementLang1) {
        this.essayRequirementLang1 = essayRequirementLang1;
    }

    public String getEssayRequirementLang1() {
        return essayRequirementLang1;
    }

    public void setEssayRequirementLang2(String essayRequirementLang2) {
        this.essayRequirementLang2 = essayRequirementLang2;
    }

    public String getEssayRequirementLang2() {
        return essayRequirementLang2;
    }

    public void setEssayRequirementLang3(String essayRequirementLang3) {
        this.essayRequirementLang3 = essayRequirementLang3;
    }

    public String getEssayRequirementLang3() {
        return essayRequirementLang3;
    }

    public void setEssayRequirementLang(String essayRequirementLang) {
        this.essayRequirementLang = essayRequirementLang;
    }

    public String getEssayRequirementLang() {
        UserSessionSource userSessionSource = AppBeans.get("cuba_UserSessionSource");
        String language = userSessionSource.getLocale().getLanguage();
        String langOrder = AppContext.getProperty("base.abstractDictionary.langOrder");

        if (langOrder != null) {
            List<String> langs = Arrays.asList(langOrder.split(";"));
            switch (langs.indexOf(language)) {
                case 0: {
                    return essayRequirementLang1;
                }
                case 1: {
                    return essayRequirementLang2;
                }
                case 2: {
                    return essayRequirementLang3;
                }
                default:
                    return essayRequirementLang1;
            }
        }

        return essayRequirementLang1;
    }

    public void setProgramNameLang(String programNameLang) {
        this.programNameLang = programNameLang;
    }

    public void setBannerLang(FileDescriptor bannerLang) {
        this.bannerLang = bannerLang;
    }

    public FileDescriptor getBannerLang() {
        UserSessionSource userSessionSource = AppBeans.get("cuba_UserSessionSource");
        String language = userSessionSource.getLocale().getLanguage();
        String langOrder = AppContext.getProperty("base.abstractDictionary.langOrder");

        if (langOrder != null) {
            List<String> langs = Arrays.asList(langOrder.split(";"));
            switch (langs.indexOf(language)) {
                case 0: {
                    return bannerLang1;
                }
                case 1: {
                    return bannerLang2;
                }
                case 2: {
                    return bannerLang3;
                }
                default:
                    return bannerLang1;
            }
        }

        return bannerLang1;
    }

    public void setParticipationRuleLang(String participationRuleLang) {
        this.participationRuleLang = participationRuleLang;
    }

    public String getParticipationRuleLang() {
        UserSessionSource userSessionSource = AppBeans.get("cuba_UserSessionSource");
        String language = userSessionSource.getLocale().getLanguage();
        String langOrder = AppContext.getProperty("base.abstractDictionary.langOrder");

        if (langOrder != null) {
            List<String> langs = Arrays.asList(langOrder.split(";"));
            switch (langs.indexOf(language)) {
                case 0: {
                    return participationRuleLang1;
                }
                case 1: {
                    return participationRuleLang2;
                }
                case 2: {
                    return participationRuleLang3;
                }
                default:
                    return participationRuleLang1;
            }
        }

        return participationRuleLang1;
    }

    public String getProgramNameLang() {
        UserSessionSource userSessionSource = AppBeans.get("cuba_UserSessionSource");
        String language = userSessionSource.getLocale().getLanguage();
        String langOrder = AppContext.getProperty("base.abstractDictionary.langOrder");

        if (langOrder != null) {
            List<String> langs = Arrays.asList(langOrder.split(";"));
            switch (langs.indexOf(language)) {
                case 0: {
                    return programNameLang1;
                }
                case 1: {
                    return programNameLang2;
                }
                case 2: {
                    return programNameLang3;
                }
                default:
                    return programNameLang1;
            }
        }

        return programNameLang1;
    }

    public void setProgramNameLang1(String programNameLang1) {
        this.programNameLang1 = programNameLang1;
    }

    public String getProgramNameLang1() {
        return programNameLang1;
    }

    public void setProgramNameLang2(String programNameLang2) {
        this.programNameLang2 = programNameLang2;
    }

    public String getProgramNameLang2() {
        return programNameLang2;
    }

    public void setProgramNameLang3(String programNameLang3) {
        this.programNameLang3 = programNameLang3;
    }

    public String getProgramNameLang3() {
        return programNameLang3;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getIsActive() {
        return isActive;
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

    public void setParticipationRuleLang1(String participationRuleLang1) {
        this.participationRuleLang1 = participationRuleLang1;
    }

    public String getParticipationRuleLang1() {
        return participationRuleLang1;
    }

    public void setParticipationRuleLang2(String participationRuleLang2) {
        this.participationRuleLang2 = participationRuleLang2;
    }

    public String getParticipationRuleLang2() {
        return participationRuleLang2;
    }

    public void setParticipationRuleLang3(String participationRuleLang3) {
        this.participationRuleLang3 = participationRuleLang3;
    }

    public String getParticipationRuleLang3() {
        return participationRuleLang3;
    }

    public void setBannerLang1(FileDescriptor bannerLang1) {
        this.bannerLang1 = bannerLang1;
    }

    public FileDescriptor getBannerLang1() {
        return bannerLang1;
    }

    public void setBannerLang2(FileDescriptor bannerLang2) {
        this.bannerLang2 = bannerLang2;
    }

    public FileDescriptor getBannerLang2() {
        return bannerLang2;
    }

    public void setBannerLang3(FileDescriptor bannerLang3) {
        this.bannerLang3 = bannerLang3;
    }

    public FileDescriptor getBannerLang3() {
        return bannerLang3;
    }

    public void setWebLink(String webLink) {
        this.webLink = webLink;
    }

    public String getWebLink() {
        return webLink;
    }

}
