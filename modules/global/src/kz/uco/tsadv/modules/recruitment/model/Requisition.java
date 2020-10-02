package kz.uco.tsadv.modules.recruitment.model;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.Listeners;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.core.sys.AppContext;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.base.entity.dictionary.DicCity;
import kz.uco.tsadv.modules.personal.dictionary.DicCostCenter;
import kz.uco.tsadv.modules.personal.group.JobGroup;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.recruitment.dictionary.DicEmploymentType;
import kz.uco.tsadv.modules.recruitment.enums.RequisitionStatus;
import kz.uco.tsadv.modules.recruitment.enums.RequisitionType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Listeners("tsadv_RequisitionListener")
@NamePattern("%s|code")
@Table(name = "TSADV_REQUISITION")
@Entity(name = "tsadv$Requisition")
public class Requisition extends AbstractParentEntity {
    private static final long serialVersionUID = -880813861388240235L;

    @NotNull
    @Column(name = "CODE", nullable = false, unique = true)
    protected String code;

    @Column(name = "EXPECTED_SALARY")
    protected BigDecimal expected_salary;

    @Column(name = "REQUISITION_TYPE", nullable = false)
    @NotNull
    protected Integer requisitionType;

    @OnDeleteInverse(DeletePolicy.UNLINK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REQUISITION_TEMPLATE_ID")
    protected Requisition requisitionTemplate;

    @NotNull
    @Temporal(TemporalType.DATE)
    @Column(name = "START_DATE", nullable = false)
    protected Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "END_DATE")
    protected Date endDate;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORGANIZATION_GROUP_ID")
    protected OrganizationGroupExt organizationGroup;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "JOB_GROUP_ID")
    protected JobGroup jobGroup;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POSITION_GROUP_ID")
    protected PositionGroupExt positionGroup;

    @OnDeleteInverse(DeletePolicy.UNLINK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MANAGER_PERSON_GROUP_ID")
    protected PersonGroupExt managerPersonGroup;

    @OnDeleteInverse(DeletePolicy.UNLINK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RECRUITER_PERSON_GROUP_ID")
    protected PersonGroupExt recruiterPersonGroup;

    @OnDeleteInverse(DeletePolicy.UNLINK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LOCATION_ID")
    protected DicCity location;

    @OnDeleteInverse(DeletePolicy.DENY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EMPLOYMENT_TYPE_ID")
    protected DicEmploymentType employmentType;

    @NotNull
    @Column(name = "OPENED_POSITIONS_COUNT", nullable = false)
    protected Double openedPositionsCount;

    @Column(name = "REQUISITION_STATUS", nullable = false)
    @NotNull
    protected Integer requisitionStatus;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "requisition")
    protected List<RequisitionCompetence> competences;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "requisition")
    protected List<RequisitionPostingChannel> postingChannels;

    @OrderBy("order")
    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "requisition")
    protected List<RequisitionHiringStep> hiringSteps;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "requisition")
    protected List<RequisitionMember> members;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "requisition")
    protected List<JobRequest> jobRequests;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "requisition")
    protected List<RequisitionQuestionnaire> questionnaires;


    @Temporal(TemporalType.DATE)
    @Column(name = "FINAL_COLLECT_DATE")
    protected Date finalCollectDate;

    @Transient
    @MetaProperty
    protected String reason;

    @Column(name = "NAME_FOR_SITE_LANG1")
    protected String nameForSiteLang1;

    @Column(name = "NAME_FOR_SITE_LANG2")
    protected String nameForSiteLang2;

    @Column(name = "NAME_FOR_SITE_LANG3")
    protected String nameForSiteLang3;

    @Column(name = "NAME_FOR_SITE_LANG4")
    protected String nameForSiteLang4;

    @Column(name = "NAME_FOR_SITE_LANG5")
    protected String nameForSiteLang5;

    @NotNull
    @Column(name = "VIDEO_INTERVIEW_REQUIRED", nullable = false)
    protected Boolean videoInterviewRequired = false;

    @NotNull
    @Column(name = "WITHOUT_OFFER", nullable = false)
    protected Boolean withoutOffer = false;


    @Lob
    @Column(name = "DESCRIPTION_LANG1")
    protected String descriptionLang1;

    @Lob
    @Column(name = "DESCRIPTION_LANG2")
    protected String descriptionLang2;

    @Lob
    @Column(name = "DESCRIPTION_LANG3")
    protected String descriptionLang3;

    @Lob
    @Column(name = "DESCRIPTION_LANG4")
    protected String descriptionLang4;

    @Lob
    @Column(name = "DESCRIPTION_LANG5")
    protected String descriptionLang5;

    @Column(name = "FOR_SUBSTITUTION", nullable = false)
    protected Boolean forSubstitution = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUBSTITUTABLE_PERSON_GROUP_ID")
    protected PersonGroupExt substitutablePersonGroup;

    @Column(name = "SUBSTITUTABLE_DESCRIPTION", length = 500)
    protected String substitutableDescription;

    @OnDeleteInverse(DeletePolicy.DENY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COST_CENTER_ID")
    protected DicCostCenter costCenter;

    @Lob
    @Column(name = "MANAGER_DESCRIPTION_LANG1")
    protected String managerDescriptionLang1;

    @Lob
    @Column(name = "MANAGER_DESCRIPTION_LANG2")
    protected String managerDescriptionLang2;

    @Lob
    @Column(name = "MANAGER_DESCRIPTION_LANG3")
    protected String managerDescriptionLang3;

    @Lob
    @Column(name = "MANAGER_DESCRIPTION_LANG4")
    protected String managerDescriptionLang4;

    @Lob
    @Column(name = "MANAGER_DESCRIPTION_LANG5")
    protected String managerDescriptionLang5;

    @Column(name = "VIEW_COUNT", nullable = false)
    protected Long viewCount;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "requisition")
    protected List<RequisitionRequirement> requisitionRequirements;

    public void setRequisitionRequirements(List<RequisitionRequirement> requisitionRequirements) {
        this.requisitionRequirements = requisitionRequirements;
    }

    public List<RequisitionRequirement> getRequisitionRequirements() {
        return requisitionRequirements;
    }


    public void setSubstitutableDescription(String substitutableDescription) {
        this.substitutableDescription = substitutableDescription;
    }

    public String getSubstitutableDescription() {
        return substitutableDescription;
    }


    public Requisition getRequisitionTemplate() {
        return requisitionTemplate;
    }

    public void setRequisitionTemplate(Requisition requisitionTemplate) {
        this.requisitionTemplate = requisitionTemplate;
    }




    public void setExpected_salary(BigDecimal expected_salary) {
        this.expected_salary = expected_salary;
    }

    public BigDecimal getExpected_salary() {
        return expected_salary;
    }


    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }

    public Long getViewCount() {
        return viewCount;
    }


    public void setManagerDescriptionLang1(String managerDescriptionLang1) {
        this.managerDescriptionLang1 = managerDescriptionLang1;
    }

    public String getManagerDescriptionLang1() {
        return managerDescriptionLang1;
    }

    public void setManagerDescriptionLang2(String managerDescriptionLang2) {
        this.managerDescriptionLang2 = managerDescriptionLang2;
    }

    public String getManagerDescriptionLang2() {
        return managerDescriptionLang2;
    }

    public void setManagerDescriptionLang3(String managerDescriptionLang3) {
        this.managerDescriptionLang3 = managerDescriptionLang3;
    }

    public String getManagerDescriptionLang3() {
        return managerDescriptionLang3;
    }

    public void setManagerDescriptionLang4(String managerDescriptionLang4) {
        this.managerDescriptionLang4 = managerDescriptionLang4;
    }

    public String getManagerDescriptionLang4() {
        return managerDescriptionLang4;
    }

    public void setManagerDescriptionLang5(String managerDescriptionLang5) {
        this.managerDescriptionLang5 = managerDescriptionLang5;
    }

    public String getManagerDescriptionLang5() {
        return managerDescriptionLang5;
    }


    public void setCostCenter(DicCostCenter costCenter) {
        this.costCenter = costCenter;
    }

    public DicCostCenter getCostCenter() {
        return costCenter;
    }


    public void setForSubstitution(Boolean forSubstitution) {
        this.forSubstitution = forSubstitution;
    }

    public Boolean getForSubstitution() {
        return forSubstitution;
    }

    public void setSubstitutablePersonGroup(PersonGroupExt substitutablePersonGroup) {
        this.substitutablePersonGroup = substitutablePersonGroup;
    }

    public PersonGroupExt getSubstitutablePersonGroup() {
        return substitutablePersonGroup;
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


    public void setWithoutOffer(Boolean withoutOffer) {
        this.withoutOffer = withoutOffer;
    }

    public Boolean getWithoutOffer() {
        return withoutOffer;
    }


    public void setVideoInterviewRequired(Boolean videoInterviewRequired) {
        this.videoInterviewRequired = videoInterviewRequired;
    }

    public Boolean getVideoInterviewRequired() {
        return videoInterviewRequired;
    }


    public void setNameForSiteLang1(String nameForSiteLang1) {
        this.nameForSiteLang1 = nameForSiteLang1;
    }

    public String getNameForSiteLang1() {
        return nameForSiteLang1;
    }

    public void setNameForSiteLang2(String nameForSiteLang2) {
        this.nameForSiteLang2 = nameForSiteLang2;
    }

    public String getNameForSiteLang2() {
        return nameForSiteLang2;
    }

    public void setNameForSiteLang3(String nameForSiteLang3) {
        this.nameForSiteLang3 = nameForSiteLang3;
    }

    public String getNameForSiteLang3() {
        return nameForSiteLang3;
    }

    public void setNameForSiteLang4(String nameForSiteLang4) {
        this.nameForSiteLang4 = nameForSiteLang4;
    }

    public String getNameForSiteLang4() {
        return nameForSiteLang4;
    }

    public void setNameForSiteLang5(String nameForSiteLang5) {
        this.nameForSiteLang5 = nameForSiteLang5;
    }

    public String getNameForSiteLang5() {
        return nameForSiteLang5;
    }


    public DicCity getLocation() {
        return location;
    }

    public void setLocation(DicCity location) {
        this.location = location;
    }


    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }


    public void setFinalCollectDate(Date finalCollectDate) {
        this.finalCollectDate = finalCollectDate;
    }

    public Date getFinalCollectDate() {
        return finalCollectDate;
    }


    @SuppressWarnings("all")
    @MetaProperty(related = "descriptionLang1,descriptionLang2,descriptionLang3,descriptionLang4,descriptionLang5")
    public String getDescriptionLang() {
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

    @SuppressWarnings("all")
    @MetaProperty(related = {"nameForSiteLang1", "nameForSiteLang2", "nameForSiteLang3", "nameForSiteLang4", "nameForSiteLang5"})
    public String getNameForSiteLang(){
        UserSessionSource userSessionSource = AppBeans.get("cuba_UserSessionSource");
        String langugae = userSessionSource.getLocale().getLanguage();
        String langOrder = AppContext.getProperty("base.abstractDictionary.langOrder");

        if (langOrder != null){
            List<String> langs = Arrays.asList(langOrder.split(";"));
            switch (langs.indexOf(langugae)){
                case 0: {
                    return nameForSiteLang1;
                }
                case 1: {
                    return nameForSiteLang2;
                }
                case 2: {
                    return nameForSiteLang3;
                }
                case 3: {
                    return nameForSiteLang4;
                }
                case 4: {
                    return nameForSiteLang5;
                }
                default:
                    return nameForSiteLang1;
            }
        }
        return nameForSiteLang1;
    }

    public RequisitionType getRequisitionType() {
        return requisitionType == null ? null : RequisitionType.fromId(requisitionType);
    }

    public void setRequisitionType(RequisitionType requisitionType) {
        this.requisitionType = requisitionType == null ? null : requisitionType.getId();
    }


    public RequisitionStatus getRequisitionStatus() {
        return requisitionStatus == null ? null : RequisitionStatus.fromId(requisitionStatus);
    }

    public void setRequisitionStatus(RequisitionStatus requisitionStatus) {
        this.requisitionStatus = requisitionStatus == null ? null : requisitionStatus.getId();
    }


    public void setQuestionnaires(List<RequisitionQuestionnaire> questionnaires) {
        this.questionnaires = questionnaires;
    }

    public List<RequisitionQuestionnaire> getQuestionnaires() {
        return questionnaires;
    }


    public void setJobRequests(List<JobRequest> jobRequests) {
        this.jobRequests = jobRequests;
    }

    public List<JobRequest> getJobRequests() {
        return jobRequests;
    }


    public void setCompetences(List<RequisitionCompetence> competences) {
        this.competences = competences;
    }

    public List<RequisitionCompetence> getCompetences() {
        return competences;
    }

    public void setPostingChannels(List<RequisitionPostingChannel> postingChannels) {
        this.postingChannels = postingChannels;
    }

    public List<RequisitionPostingChannel> getPostingChannels() {
        return postingChannels;
    }

    public void setHiringSteps(List<RequisitionHiringStep> hiringSteps) {
        this.hiringSteps = hiringSteps;
    }

    public List<RequisitionHiringStep> getHiringSteps() {
        return hiringSteps;
    }

    public void setMembers(List<RequisitionMember> members) {
        this.members = members;
    }

    public List<RequisitionMember> getMembers() {
        return members;
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

    public void setOrganizationGroup(OrganizationGroupExt organizationGroup) {
        this.organizationGroup = organizationGroup;
    }

    public OrganizationGroupExt getOrganizationGroup() {
        return organizationGroup;
    }

    public void setJobGroup(JobGroup jobGroup) {
        this.jobGroup = jobGroup;
    }

    public JobGroup getJobGroup() {
        return jobGroup;
    }

    public void setPositionGroup(PositionGroupExt positionGroup) {
        this.positionGroup = positionGroup;
    }

    public PositionGroupExt getPositionGroup() {
        return positionGroup;
    }

    public void setManagerPersonGroup(PersonGroupExt managerPersonGroup) {
        this.managerPersonGroup = managerPersonGroup;
    }

    public PersonGroupExt getManagerPersonGroup() {
        return managerPersonGroup;
    }

    public void setRecruiterPersonGroup(PersonGroupExt recruiterPersonGroup) {
        this.recruiterPersonGroup = recruiterPersonGroup;
    }

    public PersonGroupExt getRecruiterPersonGroup() {
        return recruiterPersonGroup;
    }

    public void setEmploymentType(DicEmploymentType employmentType) {
        this.employmentType = employmentType;
    }

    public DicEmploymentType getEmploymentType() {
        return employmentType;
    }

    public void setOpenedPositionsCount(Double openedPositionsCount) {
        this.openedPositionsCount = openedPositionsCount;
    }

    public Double getOpenedPositionsCount() {
        return openedPositionsCount;
    }


    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }


}