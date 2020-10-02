package kz.uco.tsadv.modules.personal.dictionary;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_BUSINESS_TRIP_TYPE")
@Entity(name = "tsadv$DicBusinessTripType")
public class DicBusinessTripType extends AbstractDictionary {
    private static final long serialVersionUID = 3194676337194946902L;
    @Column(name = "TIMESHEET_CODE")
    protected String timesheetCode;

    @Column(name = "TIMECARD_WEEKEND_CODE")
    protected String timecardWeekendCode;

    @NotNull
    @Column(name = "WORKING_DAY", nullable = false)
    protected Boolean workingDay = false;

    public void setWorkingDay(Boolean workingDay) {
        this.workingDay = workingDay;
    }

    public Boolean getWorkingDay() {
        return workingDay;
    }


    public void setTimecardWeekendCode(String timecardWeekendCode) {
        this.timecardWeekendCode = timecardWeekendCode;
    }

    public String getTimecardWeekendCode() {
        return timecardWeekendCode;
    }


    public void setTimesheetCode(String timesheetCode) {
        this.timesheetCode = timesheetCode;
    }

    public String getTimesheetCode() {
        return timesheetCode;
    }



}