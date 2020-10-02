package kz.uco.tsadv.modules.learning.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import kz.uco.tsadv.modules.learning.dictionary.DicLearningType;
import kz.uco.tsadv.modules.learning.model.*;
import kz.uco.tsadv.modules.learning.model.Budget;
import kz.uco.tsadv.modules.learning.model.Course;
import kz.uco.tsadv.modules.recruitment.enums.RequisitionStatus;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import java.util.List;
import javax.persistence.OneToMany;

@NamePattern("%s|requestNumber")
@Table(name = "TSADV_TRAINING_REQUEST")
@Entity(name = "tsadv$TrainingRequest")
public class TrainingRequest extends StandardEntity {
    private static final long serialVersionUID = -4686028692427112828L;

    @Column(name = "REQUEST_NUMBER")
    protected String requestNumber;

    @Lookup(type = LookupType.DROPDOWN)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LEARNING_TYPE_ID")
    protected DicLearningType learningType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COURSE_ID")
    protected Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BUDGET_ID")
    protected Budget budget;

    @Temporal(TemporalType.DATE)
    @Column(name = "START_DATE")
    protected Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "END_DATE")
    protected Date endDate;

    @Column(name = "STATUS")
    protected Integer status;
    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "trainingRequest")
    protected List<EnrollmentForTrainingRequest> enrollment;

    public void setEnrollment(List<EnrollmentForTrainingRequest> enrollment) {
        this.enrollment = enrollment;
    }

    public List<EnrollmentForTrainingRequest> getEnrollment() {
        return enrollment;
    }


    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }



    public void setRequestNumber(String requestNumber) {
        this.requestNumber = requestNumber;
    }

    public String getRequestNumber() {
        return requestNumber;
    }

    public void setLearningType(DicLearningType learningType) {
        this.learningType = learningType;
    }

    public DicLearningType getLearningType() {
        return learningType;
    }



    public void setBudget(Budget budget) {
        this.budget = budget;
    }

    public Budget getBudget() {
        return budget;
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

    public void setStatus(RequisitionStatus status) {
        this.status = status == null ? null : status.getId();
    }

    public RequisitionStatus getStatus() {
        return status == null ? null : RequisitionStatus.fromId(status);
    }


}