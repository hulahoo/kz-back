package kz.uco.tsadv.service;


import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.timesheet.model.Calendar;
import kz.uco.tsadv.modules.timesheet.model.StandardSchedule;
import kz.uco.tsadv.modules.timesheet.model.Timesheet;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface TimesheetService {
    String NAME = "tsadv_TimesheetService";

    void formSchedule(StandardSchedule schedule, Date month) throws Exception;

    /***
     * Этот сервис вычисляет разницу в датах с/без учёта выходных.
     * Важная вешь, праздники должны быть в рамках одного года,
     * то есть не должно быть такого что праздник с 31.12.2020 по 03.01.2021
     * */
    int getDateDiffByCalendar(String calendarCode, Date startDate, Date endDate, Boolean ignoreHolidays);

    int getDateDiffByCalendar(kz.uco.tsadv.modules.timesheet.model.Calendar calendar, Date startDate, Date endDate, Boolean ignoreHolidays);

    int getAllHolidays(Calendar calendar, Date absenceStartDate, Date absenceEndDate);

    List<Timesheet> getTimesheets(OrganizationGroupExt organizationGroup, PositionGroupExt positionGroup,
                                  PersonGroupExt personGroup, Date startDate, Date endDate, int firstResult, int maxResults, boolean loadFullData, AssignmentExt assignmentExt, Boolean enableInclusions);

    List<Timesheet> getTimesheets(Map<String, Object> params);

    int getTimesheetsCount(OrganizationGroupExt organizationGroup, PositionGroupExt positionGroup, PersonGroupExt personGroup, Date startDate, Date endDate, Boolean enableInclusions);
}