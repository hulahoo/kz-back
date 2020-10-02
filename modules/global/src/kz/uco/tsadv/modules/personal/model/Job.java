package kz.uco.tsadv.modules.personal.model;

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
import kz.uco.tsadv.modules.personal.model.InfoSalaryMarket;
import kz.uco.base.entity.abstraction.AbstractTimeBasedEntity;
import kz.uco.tsadv.modules.personal.dictionary.DicEmployeeCategory;
import kz.uco.tsadv.modules.personal.dictionary.DicJobCategory;
import kz.uco.tsadv.modules.personal.group.JobGroup;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.validation.constraints.NotNull;

@Listeners("tsadv_JobListener")
@NamePattern("%s|jobName")
@Table(name = "TSADV_JOB")
@Entity(name = "tsadv$Job")
public class Job extends AbstractTimeBasedEntity {
    private static final long serialVersionUID = 6955673680071474408L;

    @Transient
    @MetaProperty(related = {"jobNameLang1", "jobNameLang2", "jobNameLang3", "jobNameLang4", "jobNameLang5"})
    protected String jobName;

    @NotNull
    @Column(name = "JOB_NAME_LANG1", nullable = false, length = 1000)
    protected String jobNameLang1;

    @Column(name = "JOB_NAME_LANG2", length = 1000)
    protected String jobNameLang2;

    @Column(name = "JOB_NAME_LANG3", length = 1000)
    protected String jobNameLang3;

    @Column(name = "JOB_NAME_LANG4", length = 1000)
    protected String jobNameLang4;

    @Column(name = "JOB_NAME_LANG5", length = 1000)
    protected String jobNameLang5;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_ID")
    protected JobGroup group;

    @Column(name = "INSTRUCTION")
    protected byte[] instruction;


    @Column(name = "INSTRUCTION_NAME")
    protected String instructionName;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "job")
    protected List<InfoSalaryMarket> infoSalaryMarket;


    @OnDeleteInverse(DeletePolicy.DENY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EMPLOYEE_CATEGORY_ID")
    protected DicEmployeeCategory employeeCategory;

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

    @OnDeleteInverse(DeletePolicy.DENY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "JOB_CATEGORY_ID")
    protected DicJobCategory jobCategory;

    public void setJobNameLang1(String jobNameLang1) {
        this.jobNameLang1 = jobNameLang1;
    }

    public String getJobNameLang1() {
        return jobNameLang1;
    }

    public void setJobNameLang2(String jobNameLang2) {
        this.jobNameLang2 = jobNameLang2;
    }

    public String getJobNameLang2() {
        return jobNameLang2;
    }

    public void setJobNameLang3(String jobNameLang3) {
        this.jobNameLang3 = jobNameLang3;
    }

    public String getJobNameLang3() {
        return jobNameLang3;
    }

    public void setJobNameLang4(String jobNameLang4) {
        this.jobNameLang4 = jobNameLang4;
    }

    public String getJobNameLang4() {
        return jobNameLang4;
    }

    public void setJobNameLang5(String jobNameLang5) {
        this.jobNameLang5 = jobNameLang5;
    }

    public String getJobNameLang5() {
        return jobNameLang5;
    }


    public void setJobCategory(DicJobCategory jobCategory) {
        this.jobCategory = jobCategory;
    }

    public DicJobCategory getJobCategory() {
        return jobCategory;
    }


    public void setCandidateRequirementsLang1(String candidateRequirementsLang1) {
        this.candidateRequirementsLang1 = candidateRequirementsLang1;
    }

    public String getCandidateRequirementsLang1() {
        return candidateRequirementsLang1;
    }

    public void setCandidateRequirementsLang2(String candidateRequirementsLang2) {
        this.candidateRequirementsLang2 = candidateRequirementsLang2;
    }

    public String getCandidateRequirementsLang2() {
        return candidateRequirementsLang2;
    }

    public void setCandidateRequirementsLang3(String candidateRequirementsLang3) {
        this.candidateRequirementsLang3 = candidateRequirementsLang3;
    }

    public String getCandidateRequirementsLang3() {
        return candidateRequirementsLang3;
    }

    public void setCandidateRequirementsLang4(String candidateRequirementsLang4) {
        this.candidateRequirementsLang4 = candidateRequirementsLang4;
    }

    public String getCandidateRequirementsLang4() {
        return candidateRequirementsLang4;
    }

    public void setCandidateRequirementsLang5(String candidateRequirementsLang5) {
        this.candidateRequirementsLang5 = candidateRequirementsLang5;
    }

    public String getCandidateRequirementsLang5() {
        return candidateRequirementsLang5;
    }

    public void setJobDescriptionLang1(String jobDescriptionLang1) {
        this.jobDescriptionLang1 = jobDescriptionLang1;
    }

    public String getJobDescriptionLang1() {
        return jobDescriptionLang1;
    }

    public void setJobDescriptionLang2(String jobDescriptionLang2) {
        this.jobDescriptionLang2 = jobDescriptionLang2;
    }

    public String getJobDescriptionLang2() {
        return jobDescriptionLang2;
    }

    public void setJobDescriptionLang3(String jobDescriptionLang3) {
        this.jobDescriptionLang3 = jobDescriptionLang3;
    }

    public String getJobDescriptionLang3() {
        return jobDescriptionLang3;
    }

    public void setJobDescriptionLang4(String jobDescriptionLang4) {
        this.jobDescriptionLang4 = jobDescriptionLang4;
    }

    public String getJobDescriptionLang4() {
        return jobDescriptionLang4;
    }

    public void setJobDescriptionLang5(String jobDescriptionLang5) {
        this.jobDescriptionLang5 = jobDescriptionLang5;
    }

    public String getJobDescriptionLang5() {
        return jobDescriptionLang5;
    }


    public DicEmployeeCategory getEmployeeCategory() {
        return employeeCategory;
    }

    public void setEmployeeCategory(DicEmployeeCategory employeeCategory) {
        this.employeeCategory = employeeCategory;
    }






    public void setInfoSalaryMarket(List<InfoSalaryMarket> infoSalaryMarket) {
        this.infoSalaryMarket = infoSalaryMarket;
    }

    public List<InfoSalaryMarket> getInfoSalaryMarket() {
        return infoSalaryMarket;
    }


    public void setInstructionName(String instructionName) {
        this.instructionName = instructionName;
    }

    public String getInstructionName() {
        return instructionName;
    }


    public void setInstruction(byte[] instruction) {
        this.instruction = instruction;
    }

    public byte[] getInstruction() {
        return instruction;
    }


    public void setGroup(JobGroup group) {
        this.group = group;
    }

    public JobGroup getGroup() {
        return group;
    }

    public void setJobName(String jobNameLangNumber){
        this.jobName = jobNameLangNumber;
    }

    public String getJobName() {
        UserSessionSource userSessionSource = AppBeans.get("cuba_UserSessionSource");
        String language = userSessionSource.getLocale().getLanguage();
        String jobOrder = AppContext.getProperty("base.abstractDictionary.langOrder");

        if (jobOrder != null){
            List<String> langs = Arrays.asList(jobOrder.split(";"));
            switch (langs.indexOf(language)){
                case 0:{
                    return jobNameLang1;
                }
                case 1:{
                    return jobNameLang2;
                }
                case 2:{
                    return jobNameLang3;
                }
                case 3:{
                    return jobNameLang4;
                }
                case 4:{
                    return jobNameLang5;
                }
                default:
                    return jobNameLang1;
            }
        }
        return jobNameLang1;
    }

    @SuppressWarnings("all")
    @MetaProperty(related = "candidateRequirementsLang1,candidateRequirementsLang2,candidateRequirementsLang3,candidateRequirementsLang4,candidateRequirementsLang5")
    public String getCandidateRequirements() {
        UserSessionSource userSessionSource = AppBeans.get("cuba_UserSessionSource");
        String language = userSessionSource.getLocale().getLanguage();
        String langOrder = AppContext.getProperty("tal.abstractDictionary.langOrder");

        if (langOrder != null) {
            List<String> langs = Arrays.asList(langOrder.split(";"));
            switch (langs.indexOf(language)) {
                case 0: {
                    return candidateRequirementsLang1;
                }
                case 1: {
                    return candidateRequirementsLang2;
                }
                case 2: {
                    return candidateRequirementsLang3;
                }
                case 3: {
                    return candidateRequirementsLang4;
                }
                case 4: {
                    return candidateRequirementsLang5;
                }
                default:
                    return candidateRequirementsLang1;
            }
        }

        return candidateRequirementsLang1;
    }

    @SuppressWarnings("all")
    @MetaProperty(related = "jobDescriptionLang1,jobDescriptionLang2,jobDescriptionLang3,jobDescriptionLang4,jobDescriptionLang5")
    public String getJobDescription() {
        UserSessionSource userSessionSource = AppBeans.get("cuba_UserSessionSource");
        String language = userSessionSource.getLocale().getLanguage();
        String langOrder = AppContext.getProperty("tal.abstractDictionary.langOrder");

        if (langOrder != null) {
            List<String> langs = Arrays.asList(langOrder.split(";"));
            switch (langs.indexOf(language)) {
                case 0: {
                    return jobDescriptionLang1;
                }
                case 1: {
                    return jobDescriptionLang2;
                }
                case 2: {
                    return jobDescriptionLang3;
                }
                case 3: {
                    return jobDescriptionLang4;
                }
                case 4: {
                    return jobDescriptionLang5;
                }
                default:
                    return jobDescriptionLang1;
            }
        }

        return jobDescriptionLang1;
    }


}