package kz.uco.tsadv.modules.performance.model;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.performance.dictionary.DicAssessmentStatus;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Table(name = "TSADV_ASSESSMENT", uniqueConstraints = {
        @UniqueConstraint(name = "IDX_TSADV_ASSESSMENT_UNQ", columnNames = {"TEMPLATE_ID", "EMPLOYEE_PERSON_GROUP_ID"})
})
@Entity(name = "tsadv$Assessment")
public class Assessment extends AbstractParentEntity {
    private static final long serialVersionUID = 7951100236474665946L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TEMPLATE_ID")
    protected AssessmentTemplate template;

    @Temporal(TemporalType.DATE)
    @Column(name = "START_DATE", nullable = false)
    protected Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "END_DATE", nullable = false)
    protected Date endDate;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "EMPLOYEE_PERSON_GROUP_ID")
    protected PersonGroupExt employeePersonGroup;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "STATUS_ID")
    protected DicAssessmentStatus status;

    @Column(name = "GOAL_RATING", nullable = false)
    protected Double goalRating;

    @Column(name = "COMPETENCE_RATING", nullable = false)
    protected Double competenceRating;

    @Column(name = "OVERAL_RATING", nullable = false)
    protected Double overalRating;

    @Column(name = "PERFORMANCE")
    protected Double performance;

    @Column(name = "POTENTIAL")
    protected Double potential;

    @Column(name = "RISK_OF_LOSS")
    protected Double riskOfLoss;

    @Column(name = "IMPACT_OF_LOSS")
    protected Double impactOfLoss;


    @Transient
    @MetaProperty(related = "overalRating")
    public String getFormattedOveralRating() {
        return String.format("%.2f", overalRating);
    }

    @Transient
    @MetaProperty(related = "overalRating")
    public String getFormattedCompetenceRating() {
        return String.format("%.2f", competenceRating);
    }

    @Transient
    @MetaProperty(related = "goalRating")
    public String getFormattedGoalRating() {
        return String.format("%.2f", goalRating);
    }

    @NotNull
    @Column(name = "ASSESSMENT_NAME", nullable = false)
    protected String assessmentName;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "assessment", cascade = CascadeType.MERGE)
    protected List<kz.uco.tsadv.modules.performance.model.AssessmentParticipant> assessmentParticipant;

    public Double getPerformance() {
        return performance;
    }

    public void setPerformance(Double performance) {
        this.performance = performance;
    }


    public Double getPotential() {
        return potential;
    }

    public void setPotential(Double potential) {
        this.potential = potential;
    }


    public Double getRiskOfLoss() {
        return riskOfLoss;
    }

    public void setRiskOfLoss(Double riskOfLoss) {
        this.riskOfLoss = riskOfLoss;
    }


    public Double getImpactOfLoss() {
        return impactOfLoss;
    }

    public void setImpactOfLoss(Double impactOfLoss) {
        this.impactOfLoss = impactOfLoss;
    }

    public Double getOveralRating() {
        return overalRating;
    }

    public void setOveralRating(Double overalRating) {
        this.overalRating = overalRating;
    }


    public void setEmployeePersonGroup(PersonGroupExt employeePersonGroup) {
        this.employeePersonGroup = employeePersonGroup;
    }

    public PersonGroupExt getEmployeePersonGroup() {
        return employeePersonGroup;
    }


    public Double getGoalRating() {
        return goalRating;
    }

    public void setGoalRating(Double goalRating) {
        this.goalRating = goalRating;
    }

    public Double getCompetenceRating() {
        return competenceRating;
    }

    public void setCompetenceRating(Double competenceRating) {
        this.competenceRating = competenceRating;
    }


    public void setAssessmentParticipant(List<kz.uco.tsadv.modules.performance.model.AssessmentParticipant> assessmentParticipant) {
        this.assessmentParticipant = assessmentParticipant;
    }

    public List<AssessmentParticipant> getAssessmentParticipant() {
        return assessmentParticipant;
    }


    public DicAssessmentStatus getStatus() {
        return status;
    }

    public void setStatus(DicAssessmentStatus status) {
        this.status = status;
    }


    public void setAssessmentName(String assessmentName) {
        this.assessmentName = assessmentName;
    }

    public String getAssessmentName() {
        return assessmentName;
    }


    public void setTemplate(AssessmentTemplate template) {
        this.template = template;
    }

    public AssessmentTemplate getTemplate() {
        return template;
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


}