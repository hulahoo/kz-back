package kz.uco.tsadv.service;

import kz.uco.tsadv.modules.personal.dictionary.DicAbsenceType;
import kz.uco.tsadv.modules.personal.group.AssignmentGroupExt;
import kz.uco.tsadv.modules.timesheet.model.Calendar;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Date;

@Service(AbsenceRvdService.NAME)
public class AbsenceRvdServiceBean implements AbsenceRvdService {

    @Inject
    protected CalendarService calendarService;

    @Inject
    protected TimesheetService timesheetService;

    @Override
    public int countTotalHours(Date dateFrom, Date dateTo, DicAbsenceType absenceType, AssignmentGroupExt assignmentGroup) {
        final long MILLIS_IN_HOUR = (3600000);
        if (Boolean.TRUE.equals(absenceType.getIgnoreHolidays())) {
            long diff = (dateTo.getTime() - dateFrom.getTime()) / MILLIS_IN_HOUR;
            if (diff >= 0) {
                return (int) (diff + 1);
            } else {
                return 0;
            }
        } else {
            Calendar calendar = calendarService.getCalendar(assignmentGroup);
            long diff = (dateTo.getTime() - dateFrom.getTime()) / MILLIS_IN_HOUR;
            if (calendar != null && diff >= 0) {
                int holidaysNumber = timesheetService.getAllHolidays(calendar, dateFrom, dateTo);
                return (int) (diff + 1 - holidaysNumber);
            } else {
                return 0;
            }
        }
    }
}
