package kz.uco.tsadv.web.screens.vacationschedulerequest;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.gui.components.DateField;
import com.haulmont.cuba.gui.components.Form;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.entity.VacationScheduleRequest;
import kz.uco.tsadv.service.AbsenceBalanceService;
import kz.uco.tsadv.service.EmployeeService;
import kz.uco.tsadv.service.TimesheetService;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;


/**
 * User: maiha
 * Date: 23.12.2020
 * Time: 17:08
 */

@UiController("tsadv_VacationScheduleRequest.edit")
@UiDescriptor("vacation-schedule-request-edit.xml")
@EditedEntityContainer("vacationScheduleRequestDc")
@LoadDataBeforeShow
public class VacationScheduleRequestEdit extends StandardEditor<VacationScheduleRequest> {
    @Inject
    protected DataManager dataManager;
    @Inject
    protected EmployeeService employeeService;
    @Inject
    protected TimesheetService timesheetService;
    @Inject
    protected TimeSource timeSource;
    @Inject
    protected AbsenceBalanceService absenceBalanceService;
    @Inject
    protected Form form;
    @Inject
    protected DateField<Date> startDateField;
    @Inject
    protected DateField<Date> endDateField;

    @Subscribe
    protected void onAfterShow(AfterShowEvent event) {
        VacationScheduleRequest item = getEditedEntity();
        if ("DRAFT".equals(item.getStatus().getCode())) {
            startDateField.setEditable(true);
            endDateField.setEditable(true);
            item.setBalance(absenceBalanceService.getCurrentAbsenceDays(item.getPersonGroup()));

            LocalDateTime ldt = LocalDateTime.ofInstant(timeSource.currentTimestamp().toInstant(), ZoneId.systemDefault());
            Date out = Date.from(ldt.minusDays(1).atZone(ZoneId.systemDefault()).toInstant());

            startDateField.setRangeStart(out);
            startDateField.setAutofill(true);

            startDateField.addValueChangeListener(dateValueChangeEvent -> calculateDays());
            endDateField.addValueChangeListener(dateValueChangeEvent -> calculateDays());
        } else form.setEditable(false);
    }

    protected void calculateDays() {
        if (!cantCalculateDays()) {
            VacationScheduleRequest item = getEditedEntity();
            int kz = timesheetService.getDateDiffByCalendar("KZ", item.getStartDate(), item.getEndDate(), false);
            item.setAbsenceDays(kz);
        }
    }

    protected boolean cantCalculateDays() {
        VacationScheduleRequest item = getEditedEntity();
        return item.getStartDate() == null || item.getEndDate() == null;
    }

}