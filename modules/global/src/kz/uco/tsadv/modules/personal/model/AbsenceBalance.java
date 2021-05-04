package kz.uco.tsadv.modules.personal.model;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

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
    protected Double overallBalanceDays;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_FROM", nullable = false)
    protected Date dateFrom;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_TO", nullable = false)
    protected Date dateTo;

    @Column(name = "BALANCE_DAYS", nullable = false)
    protected Double balanceDays;

    @Column(name = "ADDITIONAL_BALANCE_DAYS", nullable = false)
    protected Double additionalBalanceDays;

    @Column(name = "DAYS_SPENT")
    protected Double daysSpent;

    @Column(name = "DAYS_LEFT")
    protected Double daysLeft;

    @Column(name = "EXTRA_DAYS_SPENT")
    protected Double extraDaysSpent;

    @Column(name = "EXTRA_DAYS_LEFT")
    protected Double extraDaysLeft;

    @Column(name = "LONG_ABSENCE_DAYS")
    protected Double longAbsenceDays;

    @OrderBy("createTs desc")
    @OneToMany(mappedBy = "absenceBalance")
    protected List<AbsenceToAbsenceBalance> absenceToAbsenceBalances;

    @Column(name = "ADD_BALANCE_DAYS_AIMS")
    protected Double addBalanceDaysAims;

    @Column(name = "ECOLOGICAL_DUE_DAYS")
    protected Double ecologicalDueDays;

    @Column(name = "DISABILITY_DUE_DAYS")
    protected Double disabilityDueDays;

    @Column(name = "ECOLOGICAL_DAYS_LEFT")
    protected Double ecologicalDaysLeft;

    @Column(name = "DISABILITY_DAYS_LEFT")
    protected Double disabilityDaysLeft;

    public void setBalanceDays(Double balanceDays) {
        this.balanceDays = balanceDays;
    }

    public Double getBalanceDays() {
        return balanceDays;
    }

    public void setAdditionalBalanceDays(Double additionalBalanceDays) {
        this.additionalBalanceDays = additionalBalanceDays;
    }

    public Double getAdditionalBalanceDays() {
        return additionalBalanceDays;
    }

    public void setDaysSpent(Double daysSpent) {
        this.daysSpent = daysSpent;
    }

    public Double getDaysSpent() {
        return daysSpent;
    }

    public void setExtraDaysSpent(Double extraDaysSpent) {
        this.extraDaysSpent = extraDaysSpent;
    }

    public Double getExtraDaysSpent() {
        return extraDaysSpent;
    }

    public void setLongAbsenceDays(Double longAbsenceDays) {
        this.longAbsenceDays = longAbsenceDays;
    }

    public Double getLongAbsenceDays() {
        return longAbsenceDays;
    }

    public void setAddBalanceDaysAims(Double addBalanceDaysAims) {
        this.addBalanceDaysAims = addBalanceDaysAims;
    }

    public Double getAddBalanceDaysAims() {
        return addBalanceDaysAims;
    }

    public void setEcologicalDueDays(Double ecologicalDueDays) {
        this.ecologicalDueDays = ecologicalDueDays;
    }

    public Double getEcologicalDueDays() {
        return ecologicalDueDays;
    }

    public void setDisabilityDueDays(Double disabilityDueDays) {
        this.disabilityDueDays = disabilityDueDays;
    }

    public Double getDisabilityDueDays() {
        return disabilityDueDays;
    }

    public void setOverallBalanceDays(Double overallBalanceDays) {
        this.overallBalanceDays = overallBalanceDays;
    }

    public Double getOverallBalanceDays() {
        return overallBalanceDays;
    }

    public void setDaysLeft(Double daysLeft) {
        this.daysLeft = daysLeft;
    }

    public Double getDaysLeft() {
        return daysLeft;
    }

    public void setExtraDaysLeft(Double extraDaysLeft) {
        this.extraDaysLeft = extraDaysLeft;
    }

    public Double getExtraDaysLeft() {
        return extraDaysLeft;
    }

    public Double getDisabilityDaysLeft() {
        return disabilityDaysLeft;
    }

    public void setDisabilityDaysLeft(Double disabilityDaysLeft) {
        this.disabilityDaysLeft = disabilityDaysLeft;
    }

    public Double getEcologicalDaysLeft() {
        return ecologicalDaysLeft;
    }

    public void setEcologicalDaysLeft(Double ecologicalDaysLeft) {
        this.ecologicalDaysLeft = ecologicalDaysLeft;
    }

    public void setAbsenceToAbsenceBalances(List<AbsenceToAbsenceBalance> absenceToAbsenceBalances) {
        this.absenceToAbsenceBalances = absenceToAbsenceBalances;
    }

    public List<AbsenceToAbsenceBalance> getAbsenceToAbsenceBalances() {
        return absenceToAbsenceBalances;
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

}