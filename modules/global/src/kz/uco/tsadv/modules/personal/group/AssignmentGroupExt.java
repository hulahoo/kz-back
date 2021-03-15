package kz.uco.tsadv.modules.personal.group;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.Extends;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.cuba.core.global.PersistenceHelper;
import kz.uco.base.common.BaseCommonUtils;
import kz.uco.base.entity.abstraction.IEntityGroup;
import kz.uco.base.entity.dictionary.DicCompany;
import kz.uco.base.entity.shared.AssignmentGroup;
import kz.uco.tsadv.modules.personal.model.*;
import kz.uco.tsadv.modules.timesheet.model.AssignmentSchedule;
import kz.uco.tsadv.modules.timesheet.model.OrgAnalytics;
import org.eclipse.persistence.annotations.Customizer;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@NamePattern("%s|assignmentNumber")
@Extends(AssignmentGroup.class)
@Entity(name = "base$AssignmentGroupExt")
@Customizer(AssignmentGroupExtDescriptorCustomizer.class)
public class AssignmentGroupExt extends AssignmentGroup implements IEntityGroup<AssignmentExt> {
    private static final long serialVersionUID = -7133025890951691620L;

    @OrderBy("startDate DESC")
    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "group")
    protected List<AssignmentExt> list;

    @NotNull
    @Column(name = "ASSIGNMENT_NUMBER", nullable = false)
    protected String assignmentNumber;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "assignmentGroupId")
    protected List<FlySurCharge> flySurCharge;

    @OneToMany(mappedBy = "assignmentGroup")
    protected List<Punishment> punishment;

    @OneToMany(mappedBy = "assignmentGroup")
    protected List<Awards> awards;

    @Transient
    @MetaProperty(related = "list")
    protected AssignmentExt assignment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "JOB_GROUP_ID")
    protected JobGroup jobGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GRADE_GROUP_ID")
    protected GradeGroup gradeGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID")
    protected DicCompany company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORGANIZATION_GROUP_ID")
    protected OrganizationGroupExt organizationGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POSITION_GROUP_ID")
    protected PositionGroupExt positionGroup;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "group")
    protected AssignmentExt relevantAssignment; // Текущее (для момента в машине времени) назначение

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "assignmentGroup")
    protected List<Salary> salaries;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "assignmentGroup")
    protected List<SurCharge> surCharge;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "assignmentGroup")
    protected List<AssignmentSchedule> assignmentSchedules;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ANALYTICS_ID")
    protected OrgAnalytics analytics;

    public PositionGroupExt getPositionGroup() {
        return positionGroup;
    }

    public void setPositionGroup(PositionGroupExt positionGroup) {
        this.positionGroup = positionGroup;
    }

    public DicCompany getCompany() {
        return company;
    }

    public void setCompany(DicCompany company) {
        this.company = company;
    }

    public GradeGroup getGradeGroup() {
        return gradeGroup;
    }

    public void setGradeGroup(GradeGroup gradeGroup) {
        this.gradeGroup = gradeGroup;
    }

    public JobGroup getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(JobGroup jobGroup) {
        this.jobGroup = jobGroup;
    }

    public OrganizationGroupExt getOrganizationGroup() {
        return organizationGroup;
    }

    public void setOrganizationGroup(OrganizationGroupExt organizationGroup) {
        this.organizationGroup = organizationGroup;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public void setAssignmentNumber(String assignmentNumber) {
        this.assignmentNumber = assignmentNumber;
    }

    public String getAssignmentNumber() {
        return assignmentNumber;
    }

    public void setFlySurCharge(List<FlySurCharge> flySurCharge) {
        this.flySurCharge = flySurCharge;
    }

    public List<FlySurCharge> getFlySurCharge() {
        return flySurCharge;
    }

    public void setAnalytics(OrgAnalytics analytics) {
        this.analytics = analytics;
    }

    public OrgAnalytics getAnalytics() {
        return analytics;
    }

    public List<AssignmentExt> getList() {
        return list;
    }

    public void setList(List<AssignmentExt> list) {
        this.list = list;
    }

    public void setPunishment(List<Punishment> punishment) {
        this.punishment = punishment;
    }

    public List<Punishment> getPunishment() {
        return punishment;
    }

    public void setAwards(List<Awards> awards) {
        this.awards = awards;
    }

    public List<Awards> getAwards() {
        return awards;
    }

    public void setAssignmentSchedules(List<AssignmentSchedule> assignmentSchedules) {
        this.assignmentSchedules = assignmentSchedules;
    }

    public List<AssignmentSchedule> getAssignmentSchedules() {
        return assignmentSchedules;
    }

    public void setSurCharge(List<SurCharge> surCharge) {
        this.surCharge = surCharge;
    }

    public List<SurCharge> getSurCharge() {
        return surCharge;
    }

    public void setSalaries(List<Salary> salaries) {
        this.salaries = salaries;
    }

    public List<Salary> getSalaries() {
        return salaries;
    }

    public void setAssignment(AssignmentExt assignment) {
        this.assignment = assignment;
    }

    public AssignmentExt getAssignment() {
        if (PersistenceHelper.isLoaded(this, "list") && list != null && !list.isEmpty()) {
            list.stream()
                    .filter(a -> a.getDeleteTs() == null
                            && !BaseCommonUtils.getSystemDate().before(a.getStartDate())
                            && !BaseCommonUtils.getSystemDate().after(a.getEndDate()))
                    .findFirst()
                    .ifPresent(a -> assignment = a);
        }
        return assignment;
    }

    @MetaProperty(related = "list")
    @Transient
    public String getAssignmentPersonFioWithEmployeeNumber() {
        if (getAssignment() != null) {
            return getAssignment().getPersonGroup().getPersonFioWithEmployeeNumber();
        }
        return null;
    }

    public AssignmentExt getRelevantAssignment() {
        return relevantAssignment;
    }

    public void setRelevantAssignment(AssignmentExt relevantAssignment) {
        this.relevantAssignment = relevantAssignment;
    }
}