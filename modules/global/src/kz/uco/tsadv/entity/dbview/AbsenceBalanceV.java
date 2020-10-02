package kz.uco.tsadv.entity.dbview;

import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.global.DesignSupport;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import java.util.Date;

@Table(name = "bal.hr_absence_balance")
@Entity(name = "tsadv$AbsenceBalanceV")
@DesignSupport("{'dbView':true,'generateDdl':false}")
public class AbsenceBalanceV extends StandardEntity {
    private static final long serialVersionUID = -401996553567065698L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

    @Column(name = "LEGACY_ID")
    protected String legacyID;

    @Column(name = "OVERALL_BALANCE_DAYS")
    protected Integer overallBalanceDays;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_FROM")
    protected Date dateFrom;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_TO")
    protected Date dateTo;

    @Column(name = "BALANCE_DAYS")
    protected Integer balanceDays;

    @Column(name = "ADDITIONAL_BALANCE_DAYS")
    protected Integer additionalBalanceDays;

    @Column(name = "DAYS_SPENT")
    protected Integer daysSpent;

    @Column(name = "DAYS_LEFT")
    protected Integer daysLeft;

    @Column(name = "EXTRA_DAYS_SPENT")
    protected Integer extraDaysSpent;

    @Column(name = "EXTRA_DAYS_LEFT")
    protected Integer extraDaysLeft;

    @Column(name = "LONG_ABSENCE_DAYS")
    protected Integer longAbsenceDays;

    public void setLegacyID(String legacyID) {
        this.legacyID = legacyID;
    }

    public String getLegacyID() {
        return legacyID;
    }


    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setOverallBalanceDays(Integer overallBalanceDays) {
        this.overallBalanceDays = overallBalanceDays;
    }

    public Integer getOverallBalanceDays() {
        return overallBalanceDays;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public void setBalanceDays(Integer balanceDays) {
        this.balanceDays = balanceDays;
    }

    public Integer getBalanceDays() {
        return balanceDays;
    }

    public void setAdditionalBalanceDays(Integer additionalBalanceDays) {
        this.additionalBalanceDays = additionalBalanceDays;
    }

    public Integer getAdditionalBalanceDays() {
        return additionalBalanceDays;
    }

    public void setDaysSpent(Integer daysSpent) {
        this.daysSpent = daysSpent;
    }

    public Integer getDaysSpent() {
        return daysSpent;
    }

    public void setDaysLeft(Integer daysLeft) {
        this.daysLeft = daysLeft;
    }

    public Integer getDaysLeft() {
        return daysLeft;
    }

    public void setExtraDaysSpent(Integer extraDaysSpent) {
        this.extraDaysSpent = extraDaysSpent;
    }

    public Integer getExtraDaysSpent() {
        return extraDaysSpent;
    }

    public void setExtraDaysLeft(Integer extraDaysLeft) {
        this.extraDaysLeft = extraDaysLeft;
    }

    public Integer getExtraDaysLeft() {
        return extraDaysLeft;
    }

    public void setLongAbsenceDays(Integer longAbsenceDays) {
        this.longAbsenceDays = longAbsenceDays;
    }

    public Integer getLongAbsenceDays() {
        return longAbsenceDays;
    }


}