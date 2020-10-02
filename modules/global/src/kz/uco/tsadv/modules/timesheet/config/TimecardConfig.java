package kz.uco.tsadv.modules.timesheet.config;

import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;
import com.haulmont.cuba.core.config.defaults.DefaultBoolean;
import com.haulmont.cuba.core.config.defaults.DefaultInteger;
import kz.uco.base.common.Null;

@Source(type = SourceType.DATABASE)
public interface TimecardConfig extends Config {

    /* if one person has different schedules for month - is need to merge in timecards table to one row? */
    @Property("tal.timecard.mergeTimecards")
    @DefaultBoolean(true)
    boolean getMergeTimecards();

    @Property("tal.timecard.defaultOffsetId")
    String getOffsetId();

    @Property("tal.timecard.allowableSchedulesForPosition")
    @DefaultBoolean(true)
    boolean getAllowableSchedulesForPositionOn();

    @Property("tal.timecard.multipleAbsencesToOneDay")
    @DefaultBoolean(true)
    boolean getMultipleAbsencesToOneDay();

    @Property("tal.timecard.shiftDetailsAutoGeneration")
    @DefaultBoolean(true)
    boolean getShiftDetailsAutoGeneration();

    @Property("tal.timecard.displayHoursWithMinutes")
    @DefaultBoolean(true)
    boolean getDisplayHoursWithMinutes();

    @Property("tal.timecard.hoursRound")
    Integer getHoursRound();
}
