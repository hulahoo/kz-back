package kz.uco.tsadv.modules.personal.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import kz.uco.base.entity.dictionary.DicCity;
import kz.uco.tsadv.modules.learning.dictionary.DicBudgetItem;
import kz.uco.tsadv.modules.learning.dictionary.DicLearningType;
import kz.uco.tsadv.modules.learning.model.Course;
import kz.uco.tsadv.modules.learning.model.PartyExt;
import kz.uco.tsadv.modules.personal.dictionary.DicEmployeeCategory;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.base.entity.shared.Party;
import kz.uco.tsadv.modules.personal.dictionary.DicLearningHistoryStatus;
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import java.util.List;
import javax.persistence.OneToMany;
import kz.uco.tsadv.modules.learning.model.LearningExpense;
import kz.uco.tsadv.modules.learning.model.Enrollment;
import kz.uco.tsadv.modules.learning.model.BudgetRequest;

@Table(name = "TSADV_PERSON_LEARNING_HISTORY")
@Entity(name = "tsadv$PersonLearningHistory")
public class PersonLearningHistory extends AbstractParentEntity {
    private static final long serialVersionUID = 4745964830112363337L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

    @Temporal(TemporalType.DATE)
    @NotNull
    @Column(name = "START_DATE", nullable = false)
    protected Date startDate;

    @Temporal(TemporalType.DATE)
    @NotNull
    @Column(name = "END_DATE", nullable = false)
    protected Date endDate;

    @Column(name = "HOURS")
    protected Double hours;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COURSE_ID")
    protected Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BUDGET_ITEM_ID")
    protected DicBudgetItem budgetItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EMPLOYEE_CATEGORY_ID")
    protected DicEmployeeCategory employeeCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LEARNING_TYPE_ID")
    protected DicLearningType learningType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STATUS_ID")
    protected DicLearningHistoryStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARTY_ID")
    protected PartyExt party;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LOCATION_ID")
    protected DicCity location;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "personLearningHistory")
    protected List<LearningExpense> learningExpense;

    @Column(name = "COURSE_NAME", length = 500)
    protected String courseName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ENROLLMENT_ID")
    protected Enrollment enrollment;

    @NotNull
    @Column(name = "INCOMPLETE", nullable = false)
    protected Boolean incomplete = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BUDGET_ID")
    protected BudgetRequest budget;

    public void setBudget(BudgetRequest budget) {
        this.budget = budget;
    }

    public BudgetRequest getBudget() {
        return budget;
    }


    public void setIncomplete(Boolean incomplete) {
        this.incomplete = incomplete;
    }

    public Boolean getIncomplete() {
        return incomplete;
    }


    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setEnrollment(Enrollment enrollment) {
        this.enrollment = enrollment;
    }

    public Enrollment getEnrollment() {
        return enrollment;
    }


    public void setLearningExpense(List<LearningExpense> learningExpense) {
        this.learningExpense = learningExpense;
    }

    public List<LearningExpense> getLearningExpense() {
        return learningExpense;
    }


    public DicLearningHistoryStatus getStatus() {
        return status;
    }

    public void setStatus(DicLearningHistoryStatus status) {
        this.status = status;
    }


    public PartyExt getParty() {
        return party;
    }

    public void setParty(PartyExt party) {
        this.party = party;
    }




    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
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

    public void setHours(Double hours) {
        this.hours = hours;
    }

    public Double getHours() {
        return hours;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Course getCourse() {
        return course;
    }

    public void setBudgetItem(DicBudgetItem budgetItem) {
        this.budgetItem = budgetItem;
    }

    public DicBudgetItem getBudgetItem() {
        return budgetItem;
    }

    public void setEmployeeCategory(DicEmployeeCategory employeeCategory) {
        this.employeeCategory = employeeCategory;
    }

    public DicEmployeeCategory getEmployeeCategory() {
        return employeeCategory;
    }

    public void setLearningType(DicLearningType learningType) {
        this.learningType = learningType;
    }

    public DicLearningType getLearningType() {
        return learningType;
    }

    public void setLocation(DicCity location) {
        this.location = location;
    }

    public DicCity getLocation() {
        return location;
    }


}