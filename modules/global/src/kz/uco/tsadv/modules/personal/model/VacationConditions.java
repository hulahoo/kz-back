package kz.uco.tsadv.modules.personal.model;

import kz.uco.base.entity.abstraction.AbstractTimeBasedEntity;
import kz.uco.tsadv.modules.personal.dictionary.DicAbsenceType;
import kz.uco.tsadv.modules.personal.enums.VacationDurationType;
import kz.uco.tsadv.modules.personal.group.JobGroup;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.group.VacationConditionsGroup;

import javax.persistence.*;

@Table(name = "TSADV_VACATION_CONDITIONS")
@Entity(name = "tsadv$VacationConditions")
public class VacationConditions extends AbstractTimeBasedEntity {
    private static final long serialVersionUID = -5188752310403632523L;

    @Column(name = "ADDITIONAL_DAYS", nullable = false)
    protected Integer additionalDays;

    @Column(name = "VACATION_DURATION_TYPE")
    protected String vacationDurationType;

    @Column(name = "MAIN_DAYS_NUMBER")
    protected Long mainDaysNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DAYS_TYPE_ID")
    protected DicAbsenceType daysType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_ID")
    @Deprecated
    protected VacationConditionsGroup group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POSITION_GROUP_ID")
    protected PositionGroupExt positionGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "JOB_GROUP_ID")
    protected JobGroup jobGroup;

    @Deprecated
    public VacationConditionsGroup getGroup() {
        return group;
    }

    @Deprecated
    public void setGroup(VacationConditionsGroup group) {
        this.group = group;
    }

    public void setVacationDurationType(VacationDurationType vacationDurationType) {
        this.vacationDurationType = vacationDurationType == null ? null : vacationDurationType.getId();
    }

    public VacationDurationType getVacationDurationType() {
        return vacationDurationType == null ? null : VacationDurationType.fromId(vacationDurationType);
    }

    public void setMainDaysNumber(Long mainDaysNumber) {
        this.mainDaysNumber = mainDaysNumber;
    }

    public Long getMainDaysNumber() {
        return mainDaysNumber;
    }


    public DicAbsenceType getDaysType() {
        return daysType;
    }

    public void setDaysType(DicAbsenceType daysType) {
        this.daysType = daysType;
    }

    public void setPositionGroup(PositionGroupExt positionGroup) {
        this.positionGroup = positionGroup;
    }

    public PositionGroupExt getPositionGroup() {
        return positionGroup;
    }

    public void setJobGroup(JobGroup jobGroup) {
        this.jobGroup = jobGroup;
    }

    public JobGroup getJobGroup() {
        return jobGroup;
    }

    public void setAdditionalDays(Integer additionalDays) {
        this.additionalDays = additionalDays;
    }

    public Integer getAdditionalDays() {
        return additionalDays;
    }

}