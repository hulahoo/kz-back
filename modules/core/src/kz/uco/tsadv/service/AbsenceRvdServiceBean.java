package kz.uco.tsadv.service;

import com.haulmont.cuba.core.entity.contracts.Id;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.View;
import kz.uco.base.entity.shared.PersonGroup;
import kz.uco.tsadv.modules.personal.dictionary.DicAbsenceType;
import kz.uco.tsadv.modules.personal.group.AssignmentGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.timesheet.model.Calendar;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;

@Service(AbsenceRvdService.NAME)
public class AbsenceRvdServiceBean implements AbsenceRvdService {

    @Inject
    protected CalendarService calendarService;
    @Inject
    protected TimesheetService timesheetService;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected EmployeeService employeeService;

    @Override
    public int countTotalHours(Date dateFrom, Date dateTo, UUID absenceTypeId, UUID personGroupId) {

        DicAbsenceType absenceType = dataManager.load(Id.of(absenceTypeId, DicAbsenceType.class)).view(View.LOCAL)
                .optional().orElse(null);

        AssignmentExt assignmentExt = employeeService.getAssignmentExt(personGroupId, dateFrom, "portal-assignment-group");

        final long MILLIS_IN_HOUR = (3600000);
        final long MILLIS_IN_WORK_HOUR = (1200000);
        if (Boolean.TRUE.equals(absenceType.getIgnoreHolidays())) {
            long diff = (dateTo.getTime() - dateFrom.getTime()) / MILLIS_IN_HOUR;
            if (diff >= 0) {
                return (int) (diff + 1);
            } else {
                return 2;
            }
        } else {
            Calendar calendar = calendarService.getCalendar(assignmentExt.getGroup());
            long diff = (dateTo.getTime() - dateFrom.getTime()) / MILLIS_IN_HOUR;
            if (diff >= 0) {
                if(calendar != null){
                    int holidaysNumber = timesheetService.getAllHolidays(calendar, dateFrom, dateTo) * 24;
                    return (int) (diff + 1 - holidaysNumber);
                }else{
                    return (int)(diff);
                }
            } else {
                return 1;
            }
        }
    }
}