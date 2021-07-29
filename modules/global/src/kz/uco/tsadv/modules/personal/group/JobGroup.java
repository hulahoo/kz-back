package kz.uco.tsadv.modules.personal.group;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.core.sys.AppContext;
import kz.uco.base.common.BaseCommonUtils;
import kz.uco.base.entity.abstraction.AbstractGroup;
import kz.uco.base.entity.abstraction.IEntityGroup;
import kz.uco.base.entity.dictionary.DicCompany;
import kz.uco.tsadv.modules.performance.model.PerformancePlan;
import kz.uco.tsadv.modules.personal.dictionary.DicEmployeeCategory;
import kz.uco.tsadv.modules.personal.dictionary.DicJobCategory;
import kz.uco.tsadv.modules.personal.model.*;
import kz.uco.tsadv.modules.recruitment.model.RcJobGroup;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


@Table(name = "TSADV_JOB_GROUP")
@Entity(name = "tsadv$JobGroup")
@NamePattern("%s|jobNameDefault,list")
public class JobGroup extends AbstractGroup implements IEntityGroup<Job> {
    private static final long serialVersionUID = 5049424103149725592L;

    @Transient
    @MetaProperty(related = {"jobNameLang1", "jobNameLang2", "jobNameLang3", "jobNameLang4", "jobNameLang5"})
    protected String jobNameDefault;

    @Column(name = "JOB_NAME_LANG1", length = 1000)
    private String jobNameLang1;

    @Column(name = "JOB_NAME_LANG2", length = 1000)
    private String jobNameLang2;

    @Column(name = "JOB_NAME_LANG3", length = 1000)
    private String jobNameLang3;

    @Column(name = "JOB_NAME_LANG4", length = 1000)
    private String jobNameLang4;

    @Column(name = "JOB_NAME_LANG5", length = 1000)
    private String jobNameLang5;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "group")
    protected List<Job> list;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "jobGroup")
    protected List<CompetenceElement> competence;

    @Transient
    @MetaProperty(related = "list")
    protected Job job;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "jobGroup")
    protected List<VacationConditions> vacationConditionsList;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "jobGroup")
    protected List<Case> cases;

    @JoinTable(name = "TSADV_PERFORMANCE_PLAN_JOB_GROUP_LINK",
            joinColumns = @JoinColumn(name = "JOB_GROUP_ID"),
            inverseJoinColumns = @JoinColumn(name = "PERFORMANCE_PLAN_ID"))
    @ManyToMany
    protected List<PerformancePlan> performancePlans;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "jobGroup")
    protected List<JobGroupGoalLink> goals;

    @JoinTable(name = "TSADV_RC_JOB_GROUP_JOB_GROUP_LINK",
            joinColumns = @JoinColumn(name = "JOB_GROUP_ID"),
            inverseJoinColumns = @JoinColumn(name = "RC_JOB_GROUP_ID"))
    @ManyToMany
    protected List<RcJobGroup> rcJobGroups;

    @JoinColumn(name = "EMPLOYEE_CATEGORY_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(DeletePolicy.DENY)
    private DicEmployeeCategory employeeCategory;

    @JoinColumn(name = "JOB_CATEGORY_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(DeletePolicy.DENY)
    private DicJobCategory jobCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID")
    protected DicCompany company;

    public DicCompany getCompany() {
        return company;
    }

    public void setCompany(DicCompany company) {
        this.company = company;
    }

    public DicJobCategory getJobCategory() {
        return jobCategory;
    }

    public void setJobCategory(DicJobCategory jobCategory) {
        this.jobCategory = jobCategory;
    }


    public DicEmployeeCategory getEmployeeCategory() {
        return employeeCategory;
    }

    public void setEmployeeCategory(DicEmployeeCategory employeeCategory) {
        this.employeeCategory = employeeCategory;
    }

    public String getJobNameLang5() {
        return jobNameLang5;
    }

    public void setJobNameLang5(String jobNameLang5) {
        this.jobNameLang5 = jobNameLang5;
    }

    public String getJobNameLang4() {
        return jobNameLang4;
    }

    public void setJobNameLang4(String jobNameLang4) {
        this.jobNameLang4 = jobNameLang4;
    }


    public String getJobNameLang3() {
        return jobNameLang3;
    }

    public void setJobNameLang3(String jobNameLang3) {
        this.jobNameLang3 = jobNameLang3;
    }


    public String getJobNameLang2() {
        return jobNameLang2;
    }

    public void setJobNameLang2(String jobNameLang2) {
        this.jobNameLang2 = jobNameLang2;
    }


    public String getJobNameLang1() {
        return jobNameLang1;
    }

    public void setJobNameLang1(String jobNameLang1) {
        this.jobNameLang1 = jobNameLang1;
    }

    public List<VacationConditions> getVacationConditionsList() {
        return vacationConditionsList;
    }

    public void setVacationConditionsList(List<VacationConditions> vacationConditionsList) {
        this.vacationConditionsList = vacationConditionsList;
    }

    public void setCompetence(List<CompetenceElement> competence) {
        this.competence = competence;
    }

    public List<CompetenceElement> getCompetence() {
        return competence;
    }

    public void setRcJobGroups(List<RcJobGroup> rcJobGroups) {
        this.rcJobGroups = rcJobGroups;
    }

    public List<RcJobGroup> getRcJobGroups() {
        return rcJobGroups;
    }

    public List<JobGroupGoalLink> getGoals() {
        return goals;
    }

    public void setGoals(List<JobGroupGoalLink> goals) {
        this.goals = goals;
    }

    public void setPerformancePlans(List<PerformancePlan> performancePlans) {
        this.performancePlans = performancePlans;
    }

    public List<PerformancePlan> getPerformancePlans() {
        return performancePlans;
    }

    public void setCases(List<Case> cases) {
        this.cases = cases;
    }

    public List<Case> getCases() {
        return cases;
    }

    public List<Job> getList() {
        return list;
    }

    public void setList(List<Job> list) {
        this.list = list;
    }

    public Job getJob() {
        return job != null ? job : getJobInDate(BaseCommonUtils.getSystemDate());
    }

    public Job getJobInDate(Date date) {
        if (PersistenceHelper.isLoaded(this, "list") && list != null && !list.isEmpty()) {
            list.stream()
                    .filter(j -> j.getDeleteTs() == null
                            && !date.before(j.getStartDate())
                            && !date.after(j.getEndDate()))
                    .findFirst()
                    .ifPresent(j -> job = j);
        }
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    //To display a name instead of an id in the lookup and tables #Timur Tashmatov
    @MetaProperty(related = "list")
    @Transient
    public String getJobName() {
        job = job != null ? job : getJob();
        if (job == null) {
            return "";
        }
        return job.getJobName();
    }

    public String getJobNameDefault() {
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

    public void setJobNameDefault(String jobNameDefault) {
        this.jobNameDefault = jobNameDefault;
    }
}