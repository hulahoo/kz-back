package kz.uco.tsadv.modules.personprotection;

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
import kz.uco.tsadv.modules.personal.group.JobGroup;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

import kz.uco.tsadv.modules.personprotection.dictionary.DicProtectionEquipment;

@Table(name = "TSADV_JOB_PROTECTION_EQUIPMENT")
@Entity(name = "tsadv$JobProtectionEquipment")
public class JobProtectionEquipment extends AbstractParentEntity {
    private static final long serialVersionUID = -4231608374492364034L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "JOB_GROUP_ID")
    protected JobGroup jobGroup;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSONAL_PROTECTION_EQUIPMENT_ID")
    protected DicProtectionEquipment personalProtectionEquipment;

    @NotNull
    @Column(name = "NORM_PER_YEAR", nullable = false)
    protected Integer normPerYear;

    @Temporal(TemporalType.DATE)
    @Column(name = "START_DATE")
    protected Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "END_DATE")
    protected Date endDate;
    public DicProtectionEquipment getPersonalProtectionEquipment() {
        return personalProtectionEquipment;
    }

    public void setPersonalProtectionEquipment(DicProtectionEquipment personalProtectionEquipment) {
        this.personalProtectionEquipment = personalProtectionEquipment;
    }






    public void setJobGroup(JobGroup jobGroup) {
        this.jobGroup = jobGroup;
    }

    public JobGroup getJobGroup() {
        return jobGroup;
    }

    public void setNormPerYear(Integer normPerYear) {
        this.normPerYear = normPerYear;
    }

    public Integer getNormPerYear() {
        return normPerYear;
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