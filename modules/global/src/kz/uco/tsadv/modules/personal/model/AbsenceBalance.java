package kz.uco.tsadv.modules.personal.model;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@NamePattern("%s %s %s %s|additionalBalanceDays,balanceDays,dateFrom,dateTo")
@Table(name = "TSADV_ABSENCE_BALANCE")
@Entity(name = "tsadv$AbsenceBalance")
public class AbsenceBalance extends AbstractParentEntity {
    private static final long serialVersionUID = -6154124447563375684L;

    @Lookup(type = LookupType.SCREEN, actions = {"lookup", "open", "clear"})
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

    @Column(name = "OVERALL_BALANCE_DAYS")
    protected Integer overallBalanceDays;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_FROM", nullable = false)
    protected Date dateFrom;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_TO", nullable = false)
    protected Date dateTo;

    @Column(name = "BALANCE_DAYS", nullable = false)
    protected Integer balanceDays;

    @Column(name = "ADDITIONAL_BALANCE_DAYS", nullable = false)
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

    @OrderBy("createTs desc")
    @OneToMany(mappedBy = "absenceBalance")
    protected List<AbsenceToAbsenceBalance> absenceToAbsenceBalances;

    @Column(name = "ADD_BALANCE_DAYS_AIMS")
    protected Integer addBalanceDaysAims;

    public void setAddBalanceDaysAims(Integer addBalanceDaysAims) {
        this.addBalanceDaysAims = addBalanceDaysAims;
    }

    public Integer getAddBalanceDaysAims() {
        return addBalanceDaysAims;
    }

    public void setOverallBalanceDays(Integer overallBalanceDays) {
        this.overallBalanceDays = overallBalanceDays;
    }

    public Integer getOverallBalanceDays() {
        return overallBalanceDays;
    }

    public void setAbsenceToAbsenceBalances(List<AbsenceToAbsenceBalance> absenceToAbsenceBalances) {
        this.absenceToAbsenceBalances = absenceToAbsenceBalances;
    }

    public List<AbsenceToAbsenceBalance> getAbsenceToAbsenceBalances() {
        return absenceToAbsenceBalances;
    }

    public void setDaysSpent(Integer daysSpent) {
        this.daysSpent = daysSpent;
    }

    public Integer getDaysSpent() {
        return daysSpent != null ? daysSpent : 0;
    }

    public void setDaysLeft(Integer daysLeft) {
        this.daysLeft = daysLeft;
    }

    public Integer getDaysLeft() {
        return daysLeft != null ? daysLeft : 0;
    }

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
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
        return balanceDays != null ? balanceDays : 0;
    }

    public void setAdditionalBalanceDays(Integer additionalBalanceDays) {
        this.additionalBalanceDays = additionalBalanceDays;
    }

    public Integer getAdditionalBalanceDays() {
        return additionalBalanceDays != null ? additionalBalanceDays : 0;
    }

    public Integer getExtraDaysSpent() {
        return extraDaysSpent != null ? extraDaysSpent : 0;
    }

    public void setExtraDaysSpent(Integer extraDaysSpent) {
        this.extraDaysSpent = extraDaysSpent;
    }

    public Integer getExtraDaysLeft() {
        return extraDaysLeft != null ? extraDaysLeft : 0;
    }

    public void setExtraDaysLeft(Integer extraDaysLeft) {
        this.extraDaysLeft = extraDaysLeft;
    }

    public Integer getLongAbsenceDays() {
        return longAbsenceDays != null ? longAbsenceDays : 0;
    }

    public void setLongAbsenceDays(Integer longAbsenceDays) {
        this.longAbsenceDays = longAbsenceDays;
    }
}