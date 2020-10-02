package kz.uco.tsadv.modules.timesheet.model;

import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.model.Absence;

import javax.persistence.*;

@Table(name = "TSADV_ABSENCE_TO_WORKED_HOURS_SUMMARY")
@Entity(name = "tsadv$AbsenceToWorkedHoursSummary")
public class AbsenceToWorkedHoursSummary extends AbstractParentEntity {
    private static final long serialVersionUID = -6416844577177149614L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ABSENCE_ID")
    protected Absence absence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WORKED_HOURS_SUMMARY_ID")
    protected WorkedHoursSummary workedHoursSummary;

    public void setAbsence(Absence absence) {
        this.absence = absence;
    }

    public Absence getAbsence() {
        return absence;
    }

    public void setWorkedHoursSummary(WorkedHoursSummary workedHoursSummary) {
        this.workedHoursSummary = workedHoursSummary;
    }

    public WorkedHoursSummary getWorkedHoursSummary() {
        return workedHoursSummary;
    }


}