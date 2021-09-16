package kz.uco.tsadv.modules.performance.model;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.annotation.PublishEntityChangedEvents;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.BeanLocator;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.entity.bproc.AbstractBprocRequest;
import kz.uco.tsadv.modules.performance.dictionary.DicPerformanceStage;
import kz.uco.tsadv.modules.performance.enums.CardStatusEnum;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.annotation.PostConstruct;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@PublishEntityChangedEvents
@Table(name = "TSADV_ASSIGNED_PERFORMANCE_PLAN")
@Entity(name = "tsadv$AssignedPerformancePlan")
@NamePattern("%s|requestNumber,stepStageStatus")
public class AssignedPerformancePlan extends AbstractBprocRequest {
    private static final long serialVersionUID = -5439003925414989525L;

    public static final String PROCESS_DEFINITION_KEY = "kpi";

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

    @Column(name = "STEP_STAGE_STATUS")
    protected String stepStageStatus;

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

    @Column(name = "ADJUSTED_BONUS")
    protected Double adjustedBonus;

    @Column(name = "ADJUSTED_SCORE")
    protected Double adjustedScore;

    @Column(name = "MAX_BONUS_PERCENT")
    protected Double maxBonusPercent;

    @Column(name = "PURPOSE")
    private String purpose;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FILE_ID")
    private FileDescriptor file;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LINE_MANAGER_ID")
    protected PersonGroupExt lineManager;

    public PersonGroupExt getLineManager() {
        return lineManager;
    }

    public void setLineManager(PersonGroupExt lineManager) {
        this.lineManager = lineManager;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STAGE_ID")
    protected DicPerformanceStage stage;

    public DicPerformanceStage getStage() {
        return stage;
    }

    public void setStage(DicPerformanceStage stage) {
        this.stage = stage;
    }

    public FileDescriptor getFile() {
        return file;
    }

    public void setFile(FileDescriptor file) {
        this.file = file;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public Double getMaxBonusPercent() {
        return maxBonusPercent;
    }

    public void setMaxBonusPercent(Double maxBonusPercent) {
        this.maxBonusPercent = maxBonusPercent;
    }

    public Double getAdjustedScore() {
        return adjustedScore;
    }

    public void setAdjustedScore(Double adjustedScore) {
        this.adjustedScore = adjustedScore;
    }

    public Double getAdjustedBonus() {
        return adjustedBonus;
    }

    public void setAdjustedBonus(Double adjustedBonus) {
        this.adjustedBonus = adjustedBonus;
    }

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

    public CardStatusEnum getStepStageStatus() {
        return stepStageStatus == null ? null : CardStatusEnum.fromId(stepStageStatus);
    }

    public void setStepStageStatus(CardStatusEnum stepStageStatus) {
        this.stepStageStatus = stepStageStatus == null ? null : stepStageStatus.getId();
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

    @Override
    public String getProcessDefinitionKey() {
        return PROCESS_DEFINITION_KEY;
    }

    @Override
    public String getProcessInstanceBusinessKey() {
        return super.getProcessInstanceBusinessKey() + (getStage() != null ? "/" + getStage().getCode() : null);
    }

    @PostConstruct
    public void postConstruct() {
        super.postConstruct();
        BeanLocator beanLocator = AppBeans.get(BeanLocator.class);
        this.stepStageStatus = CardStatusEnum.DRAFT.getId();
        this.setStage(beanLocator.get(CommonService.class).getEntity(DicPerformanceStage.class, "DRAFT"));
    }

    public void setNextStep() {
        if (this.stepStageStatus == null) {
            CardStatusEnum cardStatusEnum = CardStatusEnum.fromOrder(0);
            if (cardStatusEnum != null) this.stepStageStatus = cardStatusEnum.getId();
        } else {
            CardStatusEnum cardStatusEnum = CardStatusEnum.fromOrder(getStepStageStatus().getOrder() + 1);
            if (cardStatusEnum != null) this.stepStageStatus = cardStatusEnum.getId();
        }
    }

}