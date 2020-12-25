package kz.uco.tsadv.modules.personal.model;

import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.Extends;
import com.haulmont.cuba.core.entity.annotation.Listeners;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.cuba.core.global.UserSessionSource;
import kz.uco.base.entity.abstraction.IGroupedEntity;
import kz.uco.base.entity.shared.Position;
import kz.uco.tsadv.modules.personal.dictionary.DicCostCenter;
import kz.uco.tsadv.modules.personal.dictionary.DicEmployeeCategory;
import kz.uco.tsadv.modules.personal.dictionary.DicPayroll;
import kz.uco.tsadv.modules.personal.dictionary.DicPositionStatus;
import kz.uco.tsadv.modules.personal.group.GradeGroup;
import kz.uco.tsadv.modules.personal.group.JobGroup;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Listeners("tsadv_PositionExtListener")
@Extends(Position.class)
@Entity(name = "base$PositionExt")
@NamePattern("%s|positionName")
public class PositionExt extends Position implements IGroupedEntity<PositionGroupExt> {
    private static final long serialVersionUID = 4443208303116319103L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COST_CENTER_ID")
    protected DicCostCenter costCenter;

    @Lob
    @Column(name = "CANDIDATE_REQUIREMENTS_LANG1")
    protected String candidateRequirementsLang1;

    @Lob
    @Column(name = "CANDIDATE_REQUIREMENTS_LANG2")
    protected String candidateRequirementsLang2;

    @Lob
    @Column(name = "CANDIDATE_REQUIREMENTS_LANG3")
    protected String candidateRequirementsLang3;

    @Lob
    @Column(name = "CANDIDATE_REQUIREMENTS_LANG4")
    protected String candidateRequirementsLang4;

    @Lob
    @Column(name = "CANDIDATE_REQUIREMENTS_LANG5")
    protected String candidateRequirementsLang5;

    @Lob
    @Column(name = "JOB_DESCRIPTION_LANG1")
    protected String jobDescriptionLang1;

    @Lob
    @Column(name = "JOB_DESCRIPTION_LANG2")
    protected String jobDescriptionLang2;

    @Lob
    @Column(name = "JOB_DESCRIPTION_LANG3")
    protected String jobDescriptionLang3;

    @Lob
    @Column(name = "JOB_DESCRIPTION_LANG4")
    protected String jobDescriptionLang4;

    @Lob
    @Column(name = "JOB_DESCRIPTION_LANG5")
    protected String jobDescriptionLang5;

    @Transient
    @MetaProperty(related = {"positionFullNameLang1", "positionFullNameLang2", "positionFullNameLang3", "positionFullNameLang4", "positionFullNameLang5"})
    protected String positionFullName;

    @Column(name = "BAZA")
    protected String baza;

    @Column(name = "EXTRA")
    protected String extra;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "JOB_GROUP_ID")
    protected JobGroup jobGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GRADE_GROUP_ID")
    protected GradeGroup gradeGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_ID")
    protected PositionGroupExt group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PAYROLL_ID")
    protected DicPayroll payroll;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POSITION_STATUS_ID")
    protected DicPositionStatus positionStatus;

    @OnDeleteInverse(DeletePolicy.DENY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GRADE_RULE_ID")
    protected GradeRule gradeRule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORGANIZATION_GROUP_EXT_ID")
    protected OrganizationGroupExt organizationGroupExt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EMPLOYEE_CATEGORY_ID")
    protected DicEmployeeCategory employeeCategory;

//    @SuppressWarnings("all")
//    @MetaProperty(related = "positionFullNameLang1")
//    @Transient
//    public String getPositionNameLang1Reducted() {
//        if (positionFullNameLang1.length() > 48) {
//            return positionFullNameLang1.substring(0, 48) + " ...";
//        } else {
//            return positionFullNameLang1;
//        }
//    }
//
//    @SuppressWarnings("all")
//    @MetaProperty(related = "positionFullNameLang2")
//    @Transient
//    public String getPositionNameLang2Reducted() {
//        if (positionFullNameLang2.length() > 48) {
//            return positionFullNameLang2.substring(0, 48) + " ...";
//        } else {
//            return positionFullNameLang2;
//        }
//    }
//
//    @SuppressWarnings("all")
//    @MetaProperty(related = "positionFullNameLang3")
//    @Transient
//    public String getPositionNameLang3Reducted() {
//        if (positionFullNameLang3.length() > 48) {
//            return positionFullNameLang3.substring(0, 48) + " ...";
//        } else {
//            return positionFullNameLang3;
//        }
//    }

    public void setEmployeeCategory(DicEmployeeCategory employeeCategory) {
        this.employeeCategory = employeeCategory;
    }

    public DicEmployeeCategory getEmployeeCategory() {
        return employeeCategory;
    }

    public void setOrganizationGroupExt(OrganizationGroupExt organizationGroupExt) {
        this.organizationGroupExt = organizationGroupExt;
    }

    public OrganizationGroupExt getOrganizationGroupExt() {
        return organizationGroupExt;
    }

    public String getCandidateRequirementsLang1() {
        return candidateRequirementsLang1;
    }

    public void setCandidateRequirementsLang1(String candidateRequirementsLang1) {
        this.candidateRequirementsLang1 = candidateRequirementsLang1;
    }

    public String getCandidateRequirementsLang2() {
        return candidateRequirementsLang2;
    }

    public void setCandidateRequirementsLang2(String candidateRequirementsLang2) {
        this.candidateRequirementsLang2 = candidateRequirementsLang2;
    }

    public String getCandidateRequirementsLang3() {
        return candidateRequirementsLang3;
    }

    public void setCandidateRequirementsLang3(String candidateRequirementsLang3) {
        this.candidateRequirementsLang3 = candidateRequirementsLang3;
    }

    public String getCandidateRequirementsLang4() {
        return candidateRequirementsLang4;
    }

    public void setCandidateRequirementsLang4(String candidateRequirementsLang4) {
        this.candidateRequirementsLang4 = candidateRequirementsLang4;
    }

    public String getCandidateRequirementsLang5() {
        return candidateRequirementsLang5;
    }

    public void setCandidateRequirementsLang5(String candidateRequirementsLang5) {
        this.candidateRequirementsLang5 = candidateRequirementsLang5;
    }

    public String getJobDescriptionLang1() {
        return jobDescriptionLang1;
    }

    public void setJobDescriptionLang1(String jobDescriptionLang1) {
        this.jobDescriptionLang1 = jobDescriptionLang1;
    }

    public String getJobDescriptionLang2() {
        return jobDescriptionLang2;
    }

    public void setJobDescriptionLang2(String jobDescriptionLang2) {
        this.jobDescriptionLang2 = jobDescriptionLang2;
    }

    public String getJobDescriptionLang3() {
        return jobDescriptionLang3;
    }

    public void setJobDescriptionLang3(String jobDescriptionLang3) {
        this.jobDescriptionLang3 = jobDescriptionLang3;
    }

    public String getJobDescriptionLang4() {
        return jobDescriptionLang4;
    }

    public void setJobDescriptionLang4(String jobDescriptionLang4) {
        this.jobDescriptionLang4 = jobDescriptionLang4;
    }

    public String getJobDescriptionLang5() {
        return jobDescriptionLang5;
    }

    public void setJobDescriptionLang5(String jobDescriptionLang5) {
        this.jobDescriptionLang5 = jobDescriptionLang5;
    }

    public DicCostCenter getCostCenter() {
        return costCenter;
    }

    public void setCostCenter(DicCostCenter costCenter) {
        this.costCenter = costCenter;
    }

    public String getPositionFullName() {
        UserSessionSource userSessionSource = AppBeans.get("cuba_UserSessionSource");
        String language = userSessionSource.getLocale().getLanguage();
        String positionOrder = com.haulmont.cuba.core.sys.AppContext.getProperty("base.abstractDictionary.langOrder");
        if (positionOrder != null) {
            List<String> langs = Arrays.asList(positionOrder.split(";"));
            switch (langs.indexOf(language)) {
                case 0:
                    return positionFullNameLang1;
                case 1:
                    return positionFullNameLang2;
                case 2:
                    return positionFullNameLang3;
                case 3:
                    return positionFullNameLang4;
                case 4:
                    return positionFullNameLang5;
                default:
                    return positionFullNameLang1;
            }
        }
        return positionFullNameLang1;
    }

    public void setPositionFullName(String positionFullName) {
        this.positionFullName = positionFullName;
    }

    public String getBaza() {
        return baza;
    }

    public void setBaza(String baza) {
        this.baza = baza;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public JobGroup getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(JobGroup jobGroup) {
        this.jobGroup = jobGroup;
    }

    public GradeGroup getGradeGroup() {
        return gradeGroup;
    }

    public void setGradeGroup(GradeGroup gradeGroup) {
        this.gradeGroup = gradeGroup;
    }

    public PositionGroupExt getGroup() {
        return group;
    }

    public void setGroup(PositionGroupExt group) {
        this.group = group;
    }

    public DicPayroll getPayroll() {
        return payroll;
    }

    public void setPayroll(DicPayroll payroll) {
        this.payroll = payroll;
    }

    public DicPositionStatus getPositionStatus() {
        return positionStatus;
    }

    public void setPositionStatus(DicPositionStatus positionStatus) {
        this.positionStatus = positionStatus;
    }

    public GradeRule getGradeRule() {
        return gradeRule;
    }

    public void setGradeRule(GradeRule gradeRule) {
        this.gradeRule = gradeRule;
    }

    public void setPositionNames(int... indexes) {
        if (this.getJobGroup() == null || this.getStartDate() == null || this.getJobGroup().getJobInDate(this.getStartDate()) == null)
            return;
        Job job = this.getJobGroup().getJobInDate(this.getStartDate());

        for (int index : indexes) {
            this.setValue("positionNameLang" + index, job.getValue("jobNameLang" + index));
        }
    }

    public void setPositionFullNames(int... indexes) {
        if (this.getJobGroup() == null || this.getStartDate() == null || this.getJobGroup().getJobInDate(this.getStartDate()) == null)
            return;
        Job job = this.getJobGroup().getJobInDate(this.getStartDate());
        for (int index : indexes) {
            StringBuilder fullName = new StringBuilder((String) Optional.ofNullable(job.getValue("jobNameLang" + index)).orElse(""));
            if (this.getBaza() != null) {
                fullName.append('.').append(this.getBaza());
            }
            if (this.getCostCenter() != null) {
                fullName.append('.').append(this.getCostCenter().getCode());
            }
            if (this.getExtra() != null) {
                fullName.append('.').append(this.getExtra());
            }
            fullName.append('.');
            this.setValue("positionFullNameLang" + index, fullName.toString());
        }
    }
}