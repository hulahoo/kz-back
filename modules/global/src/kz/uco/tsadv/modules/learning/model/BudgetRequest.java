package kz.uco.tsadv.modules.learning.model;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.Listeners;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.cuba.core.global.PersistenceHelper;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.base.entity.dictionary.DicCity;
import kz.uco.tsadv.modules.learning.dictionary.DicBudgetItem;
import kz.uco.tsadv.modules.learning.dictionary.DicBudgetStatus;
import kz.uco.tsadv.modules.learning.dictionary.DicLearningType;
import kz.uco.tsadv.modules.learning.dictionary.DicTrainingMethod;
import kz.uco.tsadv.modules.personal.dictionary.DicEmployeeCategory;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import kz.uco.tsadv.modules.learning.dictionary.DicRequiredEducation;

@Listeners("tsadv_BudgetRequestListener")
@NamePattern("%s|id")
@Table(name = "TSADV_BUDGET_REQUEST")
@Entity(name = "tsadv$BudgetRequest")
public class BudgetRequest extends AbstractParentEntity {
    private static final long serialVersionUID = 8913011297796991438L;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "BUDGET_ID")
    protected Budget budget;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REQUIRED_EDUCATION_ID")
    protected DicRequiredEducation requiredEducation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EMPLOYEE_CATEGORY_ID")
    protected DicEmployeeCategory employeeCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRAINING_METHOD_ID")
    protected DicTrainingMethod trainingMethod;

    @Column(name = "TRAINING_SUBJECT")
    protected String trainingSubject;

    @Column(name = "EDUCATION_ON_WORK")
    protected Boolean educationOnWork;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COURSE_ID")
    protected Course course;

    @Column(name = "COURSE_NAME")
    protected String courseName;

    @OnDeleteInverse(DeletePolicy.DENY)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "LEARNING_TYPE_ID")
    protected DicLearningType learningType;

    @Column(name = "EMPLOYEES_COUNT")
    protected Integer employeesCount;

    @Temporal(TemporalType.DATE)
    @Column(name = "MONTH_")
    protected Date month;

    @Column(name = "LEARNING_COSTS")
    protected Double learningCosts;

    @Column(name = "TRIP_COSTS")
    protected Double tripCosts;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROVIDER_COMPANY_ID")
    protected PartyExt providerCompany;

    @Column(name = "COMMENT_", length = 2000)
    protected String comment;

    @OnDeleteInverse(DeletePolicy.DENY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INITIATOR_PERSON_GROUP_ID")
    protected PersonGroupExt initiatorPersonGroup;

    @OnDeleteInverse(DeletePolicy.DENY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORGANIZATION_GROUP_ID")
    protected OrganizationGroupExt organizationGroup;

    @OnDeleteInverse(DeletePolicy.DENY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STATUS_ID")
    protected DicBudgetStatus status;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "BUDGET_HEADER_ID")
    protected BudgetHeader budgetHeader;

    @Column(name = "REASON")
    protected String reason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CITY_ID")
    protected DicCity city;

    @Column(name = "DAY_")
    protected Integer day;

    @Column(name = "HOUR_")
    protected Integer hour;

    @Column(name = "BUSINESS_TRIP_EMPLOYEE")
    protected Integer businessTripEmployee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BUDGET_ITEM_ID")
    protected DicBudgetItem budgetItem;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "budgetRequest")
    protected List<BudgetRequestItem> budgetRequestItems;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "budgetRequest")
    protected List<BudgetRequestDetail> budgetRequestDetail;
    public DicRequiredEducation getRequiredEducation() {
        return requiredEducation;
    }

    public void setRequiredEducation(DicRequiredEducation requiredEducation) {
        this.requiredEducation = requiredEducation;
    }



    public void setBudgetRequestItems(List<BudgetRequestItem> budgetRequestItems) {
        this.budgetRequestItems = budgetRequestItems;
    }

    public List<BudgetRequestItem> getBudgetRequestItems() {
        return budgetRequestItems;
    }

    public void setBudgetRequestDetail(List<BudgetRequestDetail> budgetRequestDetail) {
        this.budgetRequestDetail = budgetRequestDetail;
    }

    public List<BudgetRequestDetail> getBudgetRequestDetail() {
        return budgetRequestDetail;
    }


    public void setBudgetItem(DicBudgetItem budgetItem) {
        this.budgetItem = budgetItem;
    }

    public DicBudgetItem getBudgetItem() {
        return budgetItem;
    }


    public void setBudgetHeader(BudgetHeader budgetHeader) {
        this.budgetHeader = budgetHeader;
    }

    public BudgetHeader getBudgetHeader() {
        return budgetHeader;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setCity(DicCity city) {
        this.city = city;
    }

    public DicCity getCity() {
        return city;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public Integer getDay() {
        return day;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    public Integer getHour() {
        return hour;
    }

    public void setBusinessTripEmployee(Integer businessTripEmployee) {
        this.businessTripEmployee = businessTripEmployee;
    }

    public Integer getBusinessTripEmployee() {
        return businessTripEmployee;
    }


    public void setProviderCompany(PartyExt providerCompany) {
        this.providerCompany = providerCompany;
    }

    public PartyExt getProviderCompany() {
        return providerCompany;
    }





    public void setEmployeeCategory(DicEmployeeCategory employeeCategory) {
        this.employeeCategory = employeeCategory;
    }

    public DicEmployeeCategory getEmployeeCategory() {
        return employeeCategory;
    }


    public void setTrainingMethod(DicTrainingMethod trainingMethod) {
        this.trainingMethod = trainingMethod;
    }

    public DicTrainingMethod getTrainingMethod() {
        return trainingMethod;
    }

    public void setTrainingSubject(String trainingSubject) {
        this.trainingSubject = trainingSubject;
    }

    public String getTrainingSubject() {
        return trainingSubject;
    }

    public void setEducationOnWork(Boolean educationOnWork) {
        this.educationOnWork = educationOnWork;
    }

    public Boolean getEducationOnWork() {
        return educationOnWork;
    }


    public void setOrganizationGroup(OrganizationGroupExt organizationGroup) {
        this.organizationGroup = organizationGroup;
    }

    public OrganizationGroupExt getOrganizationGroup() {
        return organizationGroup;
    }


    public void setStatus(DicBudgetStatus status) {
        this.status = status;
    }

    public DicBudgetStatus getStatus() {
        return status;
    }


    public void setBudget(Budget budget) {
        this.budget = budget;
    }

    public Budget getBudget() {
        return budget;
    }



    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public void setInitiatorPersonGroup(PersonGroupExt initiatorPersonGroup) {
        this.initiatorPersonGroup = initiatorPersonGroup;
    }

    public PersonGroupExt getInitiatorPersonGroup() {
        return initiatorPersonGroup;
    }


    public void setLearningType(DicLearningType learningType) {
        this.learningType = learningType;
    }

    public DicLearningType getLearningType() {
        return learningType;
    }

    public void setEmployeesCount(Integer employeesCount) {
        this.employeesCount = employeesCount;
    }

    public Integer getEmployeesCount() {
        return employeesCount;
    }

    public void setMonth(Date month) {
        this.month = month;
    }

    public Date getMonth() {
        return month;
    }

    public void setLearningCosts(Double learningCosts) {
        this.learningCosts = learningCosts;
    }

    public Double getLearningCosts() {
        return learningCosts;
    }

    public void setTripCosts(Double tripCosts) {
        this.tripCosts = tripCosts;
    }

    public Double getTripCosts() {
        return tripCosts;
    }


    public void setCourse(Course course) {
        this.course = course;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseName() {
        return courseName;
    }

    @MetaProperty(related = {"courseName", "course"})
    public String getCalcCourseName() {
        if (getCourseName() != null)
            return getCourseName();
        else if (PersistenceHelper.isLoaded(this, "course")
                && this.course != null
                && PersistenceHelper.isLoaded(this.course, "name"))
            return getCourse().getName();

        return null;
    }

}