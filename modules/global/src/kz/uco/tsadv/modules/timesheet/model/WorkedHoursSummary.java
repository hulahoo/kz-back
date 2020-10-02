package kz.uco.tsadv.modules.timesheet.model;

import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NumberFormat;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.dictionary.DicAbsenceType;
import kz.uco.tsadv.modules.personal.dictionary.DicBusinessTripType;
import kz.uco.tsadv.modules.personal.model.Absence;
import kz.uco.tsadv.modules.personal.model.BusinessTrip;
import kz.uco.tsadv.modules.personal.model.Order;
import kz.uco.tsadv.modules.timesheet.dictionary.DicScheduleElementType;
import kz.uco.tsadv.service.TimecardService;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Table(name = "TSADV_WORKED_HOURS_SUMMARY")
@Entity(name = "tsadv$WorkedHoursSummary")
public class WorkedHoursSummary extends AbstractParentEntity {
    private static final long serialVersionUID = 2400320446970878641L;
    public static final String SICKNESS_ABSENCE_TYPE_CODE = "MATERNITY_SICK|SICKNESS|PROFSICK|CHILDSICK";

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TIMECARD_HEADER_ID")
    protected TimecardHeader timecardHeader;

    @Column(name = "DISPLAY_VALUE")
    protected String displayValue;

    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "workedHoursSummary")
    protected List<AbsenceToWorkedHoursSummary> absenceToWorkedHoursSummaryList;

    @Temporal(TemporalType.DATE)
    @NotNull
    @Column(name = "WORKED_DATE", nullable = false)
    protected Date workedDate;

    @NumberFormat(pattern = "#######,###")
    @NotNull
    @Column(name = "HOURS", nullable = false)
    protected Double hours;

    @Temporal(TemporalType.DATE)
    @Column(name = "TIME_IN")
    protected Date timeIn;

    @Temporal(TemporalType.DATE)
    @Column(name = "TIME_OUT")
    protected Date timeOut;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SHIFT_ID")
    protected Shift shift;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SCHEDULE_ELEMENT_TYPE_ID")
    protected DicScheduleElementType scheduleElementType;

    @Column(name = "CORRECTION_FLAG")
    protected Boolean correctionFlag;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BUSSINESS_TRIP_ID")
    protected BusinessTrip bussinessTrip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ID")
    protected Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ABSENCE_ID")
    protected Absence absence;

    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public void setAbsenceToWorkedHoursSummaryList(List<AbsenceToWorkedHoursSummary> absenceToWorkedHoursSummaryList) {
        this.absenceToWorkedHoursSummaryList = absenceToWorkedHoursSummaryList;
    }

    public List<AbsenceToWorkedHoursSummary> getAbsenceToWorkedHoursSummaryList() {
        return absenceToWorkedHoursSummaryList;
    }

    public void setAbsence(Absence absence) {
        this.absence = absence;
    }

    public Absence getAbsence() {
        return absence;
    }

    public Double getHours() {
        return hours;
    }

    public void setHours(Double hours) {
        this.hours = hours;
    }

    public void setBussinessTrip(BusinessTrip bussinessTrip) {
        this.bussinessTrip = bussinessTrip;
    }

    public BusinessTrip getBussinessTrip() {
        return bussinessTrip;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Order getOrder() {
        return order;
    }

    public void setCorrectionFlag(Boolean correctionFlag) {
        this.correctionFlag = correctionFlag;
    }

    public Boolean getCorrectionFlag() {
        return correctionFlag;
    }

    public void setTimecardHeader(TimecardHeader timecardHeader) {
        this.timecardHeader = timecardHeader;
    }

    public TimecardHeader getTimecardHeader() {
        return timecardHeader;
    }

    public void setWorkedDate(Date workedDate) {
        this.workedDate = workedDate;
    }

    public Date getWorkedDate() {
        return workedDate;
    }

    public void setTimeIn(Date timeIn) {
        this.timeIn = timeIn;
    }

    public Date getTimeIn() {
        return timeIn;
    }

    public void setTimeOut(Date timeOut) {
        this.timeOut = timeOut;
    }

    public Date getTimeOut() {
        return timeOut;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }

    public Shift getShift() {
        return shift;
    }

    public void setScheduleElementType(DicScheduleElementType scheduleElementType) {
        this.scheduleElementType = scheduleElementType;
    }

    public DicScheduleElementType getScheduleElementType() {
        return scheduleElementType;
    }

    @MetaProperty
    public String getTimecardRepresentation() {
        StringBuilder value;
        TimecardService timecardService = AppBeans.get(TimecardService.class);
        if (displayValue != null) {
            value = new StringBuilder(displayValue);
            if (" ".equals(displayValue)) {
                return value.toString();
            }
        } else {
            value = new StringBuilder(timecardService.getTimecardRepresentation(hours));
        }

        String code = scheduleElementType.getCode();
        if (!code.equalsIgnoreCase("WORK_HOURS")) {
            String caption = scheduleElementType.getShortName();
            if (hours > 0d) {
                caption += " " + hours;
            }
            value = new StringBuilder(caption);
        }

        if (absenceToWorkedHoursSummaryList != null && !absenceToWorkedHoursSummaryList.isEmpty()) {
            value = new StringBuilder();
            Absence sicknessAbsence = null;
            boolean isWorkingDay = absence.getType().getIsWorkingDay();
            for (AbsenceToWorkedHoursSummary absenceToWorkedHoursSummary : absenceToWorkedHoursSummaryList) {
                Absence summaryAbsence = absenceToWorkedHoursSummary.getAbsence();
                if (summaryAbsence.getType().getCode() != null
                        && summaryAbsence.getType().getCode().matches(SICKNESS_ABSENCE_TYPE_CODE)) {
                    sicknessAbsence = summaryAbsence;
                    break;
                }
                value.append(getAbsenceStringRepresentation(summaryAbsence)).append("/");
            }
            if (sicknessAbsence != null) {
                value = new StringBuilder(getAbsenceStringRepresentation(sicknessAbsence));
                if (sicknessAbsence.getType().getIsWorkingDay()) {
                    value.append("/").append(timecardService.getTimecardRepresentation(hours));
                }
            } else {
                value = new StringBuilder(value.substring(0, value.length() - 1));
                if (isWorkingDay) {
                    value.append("/").append(timecardService.getTimecardRepresentation(hours));
                }
            }
        } else if (absence != null) {
            value = new StringBuilder(getAbsenceStringRepresentation(absence));
            if (absence.getType().getIsWorkingDay()) {
                value.append("/").append(timecardService.getTimecardRepresentation(hours));
            }
        }

        Optional<BusinessTrip> businessTripOptional = Optional.ofNullable(bussinessTrip);
        if (businessTripOptional.isPresent()) {
            DicBusinessTripType businessTripType = businessTripOptional.get().getType();
            if (businessTripType != null) {
                value = new StringBuilder(businessTripType.getTimesheetCode());
                if (code.equalsIgnoreCase("WEEKEND")) {
                    value = new StringBuilder(businessTripType.getTimecardWeekendCode());
                }
            }
        }

        if (code.equals("WORK_HOURS")
                && hours == 0
                && bussinessTrip == null
                && absence == null
                && (absenceToWorkedHoursSummaryList == null
                || absenceToWorkedHoursSummaryList.isEmpty())) {
            value = new StringBuilder("В"); //todo подумать, как не зашивать короткое наименование выходного
        }

        return value.toString();
    }

    private String getAbsenceStringRepresentation(Absence absence) {
        DicAbsenceType absenceType = absence.getType();
        if (absenceType != null
                && absenceType.getTimesheetCode() != null
                && Boolean.TRUE.equals(absenceType.getDisplayAbsence())) {
            return absenceType.getTimesheetCode();
        }
        return "";
    }
}