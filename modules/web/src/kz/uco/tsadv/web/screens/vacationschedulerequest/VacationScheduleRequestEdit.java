package kz.uco.tsadv.web.screens.vacationschedulerequest;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.components.DateField;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.entity.VacationScheduleRequest;
import kz.uco.tsadv.service.EmployeeService;
import kz.uco.tsadv.service.TimesheetService;

import javax.inject.Inject;
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
    protected DateField<Date> startDateField;
    @Inject
    protected DateField<Date> endDateField;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected EmployeeService employeeService;
    @Inject
    protected TimesheetService timesheetService;

    @Subscribe
    protected void onAfterInit(AfterInitEvent event) {
        startDateField.addValueChangeListener(dateValueChangeEvent -> {
            if (cantCalculateDays()) {
                return;
            }
            calculateDays();
        });

        endDateField.addValueChangeListener(dateValueChangeEvent -> {
            if (cantCalculateDays()) {
                return;
            }
            calculateDays();
        });
    }

    protected void calculateDays() {
        VacationScheduleRequest item = getEditedEntity();
        int kz = timesheetService.getDateDiffByCalendar("KZ", item.getStartDate(), item.getEndDate(), false);
        item.setAbsenceDays(kz);
    }

    protected boolean cantCalculateDays() {
        VacationScheduleRequest item = getEditedEntity();
        return item.getStartDate() == null || item.getEndDate() == null;
    }


    @Subscribe
    protected void onAfterShow(AfterShowEvent event) {
        VacationScheduleRequest item = getEditedEntity();
        if ("DRAFT".equals(item.getStatus().getCode())) {
            startDateField.setEditable(true);
            endDateField.setEditable(true);
        }
    }


}