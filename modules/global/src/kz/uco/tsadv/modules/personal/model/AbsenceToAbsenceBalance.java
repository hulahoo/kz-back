package kz.uco.tsadv.modules.personal.model;

import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.*;

@Table(name = "TSADV_ABSENCE_TO_ABSENCE_BALANCE", uniqueConstraints = {
    @UniqueConstraint(name = "IDX_TSADV_ABSENCE_TO_ABSENCE_BALANCE_UNQ", columnNames = {"ABSENCE_ID", "ABSENCE_BALANCE_ID"})
})
@Entity(name = "tsadv$AbsenceToAbsenceBalance")
public class AbsenceToAbsenceBalance extends StandardEntity {
    private static final long serialVersionUID = -2080395859981959216L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ABSENCE_ID")
    protected Absence absence;

    @Column(name = "ADDITIONAL_ABSENCE_DAYS")
    protected Integer additionalAbsenceDays;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ABSENCE_BALANCE_ID")
    protected AbsenceBalance absenceBalance;

    @Column(name = "ABSENCE_DAYS")
    protected Integer absenceDays;

    public void setAdditionalAbsenceDays(Integer additionalAbsenceDays) {
        this.additionalAbsenceDays = additionalAbsenceDays;
    }

    public Integer getAdditionalAbsenceDays() {
        return additionalAbsenceDays;
    }


    public Absence getAbsence() {
        return absence;
    }

    public void setAbsence(Absence absence) {
        this.absence = absence;
    }

    public void setAbsenceBalance(AbsenceBalance absenceBalance) {
        this.absenceBalance = absenceBalance;
    }

    public AbsenceBalance getAbsenceBalance() {
        return absenceBalance;
    }

    public void setAbsenceDays(Integer absenceDays) {
        this.absenceDays = absenceDays;
    }

    public Integer getAbsenceDays() {
        return absenceDays;
    }


}