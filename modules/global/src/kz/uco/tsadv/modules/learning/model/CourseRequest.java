package kz.uco.tsadv.modules.learning.model;

import kz.uco.tsadv.global.dictionary.DicMonth;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

import javax.persistence.*;

@Table(name = "TSADV_COURSE_REQUEST")
@Entity(name = "tsadv$CourseRequest")
public class CourseRequest extends AbstractParentEntity {
    private static final long serialVersionUID = -7453348576147134583L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "BUDGET_ID")
    protected kz.uco.tsadv.modules.learning.model.Budget budget;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "COURSE_ID")
    protected Course course;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ORGANIZATION_ID")
    protected OrganizationGroupExt organization;

    @Column(name = "PERSON_COUNT", nullable = false)
    protected Integer personCount;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MONTH_ID")
    protected DicMonth month;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "INITIATOR_ID")
    protected PersonGroupExt initiator;

    @Column(name = "REASON", length = 2000)
    protected String reason;

    @Column(name = "COURSE_NAME")
    protected String courseName;

    @Column(name = "BUDGET_SUM")
    protected Long budgetSum;

    public DicMonth getMonth() {
        return month;
    }

    public void setMonth(DicMonth month) {
        this.month = month;
    }


    public void setBudget(kz.uco.tsadv.modules.learning.model.Budget budget) {
        this.budget = budget;
    }

    public Budget getBudget() {
        return budget;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Course getCourse() {
        return course;
    }

    public void setOrganization(OrganizationGroupExt organization) {
        this.organization = organization;
    }

    public OrganizationGroupExt getOrganization() {
        return organization;
    }

    public void setPersonCount(Integer personCount) {
        this.personCount = personCount;
    }

    public Integer getPersonCount() {
        return personCount;
    }

    public void setInitiator(PersonGroupExt initiator) {
        this.initiator = initiator;
    }

    public PersonGroupExt getInitiator() {
        return initiator;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setBudgetSum(Long budgetSum) {
        this.budgetSum = budgetSum;
    }

    public Long getBudgetSum() {
        return budgetSum;
    }


}