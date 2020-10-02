package kz.uco.tsadv.modules.timesheet.model.dto;

import kz.uco.tsadv.modules.timesheet.model.ScheduleDetail;
import kz.uco.tsadv.modules.timesheet.model.WorkedHoursDetailed;

import java.io.Serializable;
import java.util.Date;

public class NightPartDTO implements Serializable{

    WorkedHoursDetailed workedHoursDetailed;
    ScheduleDetail scheduleDetail;
    Date nightHoursFrom;
    Date nightHoursTo;

    public NightPartDTO(WorkedHoursDetailed workedHoursDetailed, ScheduleDetail scheduleDetail, Date nightHoursFrom, Date nightHoursTo) {
        this.workedHoursDetailed = workedHoursDetailed;
        this.scheduleDetail = scheduleDetail;
        this.nightHoursFrom = nightHoursFrom;
        this.nightHoursTo = nightHoursTo;
    }

    public ScheduleDetail getScheduleDetail() {
        return scheduleDetail;
    }

    public void setScheduleDetail(ScheduleDetail scheduleDetail) {
        this.scheduleDetail = scheduleDetail;
    }

    public WorkedHoursDetailed getWorkedHoursDetailed() {
        return workedHoursDetailed;
    }

    public void setWorkedHoursDetailed(WorkedHoursDetailed workedHoursDetailed) {
        this.workedHoursDetailed = workedHoursDetailed;
    }

    public Date getNightHoursFrom() {
        return nightHoursFrom;
    }

    public void setNightHoursFrom(Date nightHoursFrom) {
        this.nightHoursFrom = nightHoursFrom;
    }

    public Date getNightHoursTo() {
        return nightHoursTo;
    }

    public void setNightHoursTo(Date nightHoursTo) {
        this.nightHoursTo = nightHoursTo;
    }
}
