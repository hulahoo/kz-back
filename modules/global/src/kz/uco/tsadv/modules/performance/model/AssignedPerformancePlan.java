package kz.uco.tsadv.modules.performance.model;

import com.haulmont.cuba.core.entity.StandardEntity;
import kz.uco.tsadv.modules.performance.enums.CardStatusEnum;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "TSADV_ASSIGNED_PERFORMANCE_PLAN")
@Entity(name = "tsadv$AssignedPerformancePlan")
public class AssignedPerformancePlan extends StandardEntity {
    private static final long serialVersionUID = -5439003925414989525L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERFORMANCE_PLAN_ID")
    protected PerformancePlan performancePlan;

    @Column(name = "RESULT")
    protected Double result;

    @Column(name = "GZP")
    protected BigDecimal gzp;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ASSIGNED_PERSON_ID")
    protected PersonGroupExt assignedPerson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ASSIGNED_BY_ID")
    protected PersonGroupExt assigned_by;

    @Column(name = "STATUS")
    protected String status;

    @Temporal(TemporalType.DATE)
    @Column(name = "START_DATE")
    protected Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "END_DATE")
    protected Date endDate;

    @Column(name = "KPI_SCORE")
    protected Double kpiScore;

    @Column(name = "EXTRA_POINT")
    protected Double extraPoint;

    @Column(name = "FINAL_SCORE")
    protected Double finalScore;

    @Column(name = "COMPANY_BONUS")
    protected Double companyBonus;

    @Column(name = "PERSONAL_BONUS")
    protected Double personalBonus;

    @Column(name = "FINAL_BONUS")
    protected Double finalBonus;

    @Column(name = "MAX_BONUS")
    protected BigDecimal maxBonus;

    public BigDecimal getMaxBonus() {
        return maxBonus;
    }

    public void setMaxBonus(BigDecimal maxBonus) {
        this.maxBonus = maxBonus;
    }

    public Double getFinalBonus() {
        return finalBonus;
    }

    public void setFinalBonus(Double finalBonus) {
        this.finalBonus = finalBonus;
    }

    public void setKpiScore(Double kpiScore) {
        this.kpiScore = kpiScore;
    }

    public Double getKpiScore() {
        return kpiScore;
    }

    public void setExtraPoint(Double extraPoint) {
        this.extraPoint = extraPoint;
    }

    public Double getExtraPoint() {
        return extraPoint;
    }

    public Double getPersonalBonus() {
        return personalBonus;
    }

    public void setPersonalBonus(Double personalBonus) {
        this.personalBonus = personalBonus;
    }

    public Double getCompanyBonus() {
        return companyBonus;
    }

    public void setCompanyBonus(Double companyBonus) {
        this.companyBonus = companyBonus;
    }

    public Double getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(Double finalScore) {
        this.finalScore = finalScore;
    }

    public void setResult(Double result) {
        this.result = result;
    }

    public Double getResult() {
        return result;
    }

    public void setGzp(BigDecimal gzp) {
        this.gzp = gzp;
    }

    public BigDecimal getGzp() {
        return gzp;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public CardStatusEnum getStatus() {
        return status == null ? null : CardStatusEnum.fromId(status);
    }

    public void setStatus(CardStatusEnum status) {
        this.status = status == null ? null : status.getId();
    }

    public void setPerformancePlan(PerformancePlan performancePlan) {
        this.performancePlan = performancePlan;
    }

    public PerformancePlan getPerformancePlan() {
        return performancePlan;
    }

    public void setAssignedPerson(PersonGroupExt assignedPerson) {
        this.assignedPerson = assignedPerson;
    }

    public PersonGroupExt getAssignedPerson() {
        return assignedPerson;
    }

    public void setAssigned_by(PersonGroupExt assigned_by) {
        this.assigned_by = assigned_by;
    }

    public PersonGroupExt getAssigned_by() {
        return assigned_by;
    }


}