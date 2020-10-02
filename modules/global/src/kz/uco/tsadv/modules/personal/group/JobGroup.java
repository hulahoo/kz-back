package kz.uco.tsadv.modules.personal.group;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.cuba.core.global.PersistenceHelper;
import kz.uco.base.common.BaseCommonUtils;
import kz.uco.base.entity.abstraction.AbstractGroup;
import kz.uco.tsadv.modules.performance.model.PerformancePlan;
import kz.uco.tsadv.modules.personal.model.*;
import kz.uco.tsadv.modules.recruitment.model.RcJobGroup;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@NamePattern("%s|id")
@Table(name = "TSADV_JOB_GROUP")
@Entity(name = "tsadv$JobGroup")
public class JobGroup extends AbstractGroup {
    private static final long serialVersionUID = 5049424103149725592L;

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
    public String getJobName() {
        job = job != null ? job : getJob();
        if (job == null) {
            return "";
        }
        return job.getJobName();
    }

}