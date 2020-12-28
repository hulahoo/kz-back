package kz.uco.tsadv.web.screens.vacationschedulerequest;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.gui.components.DateField;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.entity.AbsenceRequestStatus;
import kz.uco.tsadv.entity.VacationScheduleRequest;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.service.EmployeeNumberService;
import kz.uco.tsadv.service.EmployeeService;

import javax.inject.Inject;
import java.util.Date;
import java.util.concurrent.TimeUnit;


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
    protected EmployeeNumberService employeeNumberService;
    @Inject
    protected EmployeeService employeeService;
    @Inject
    protected CommonService commonService;
    @Inject
    protected InstanceContainer<VacationScheduleRequest> vacationScheduleRequestDc;
    @Inject
    protected DateField<Date> startDateField;
    @Inject
    protected TimeSource timeSource;
    @Inject
    protected UserSession userSession;
    @Inject
    protected DateField<Date> endDateField;
    @Inject
    protected DataManager dataManager;

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

    private void calculateDays() {
        VacationScheduleRequest item = getEditedEntity();

        long diffInMillies = Math.abs(item.getEndDate().getTime() - item.getStartDate().getTime());
        long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

        item.setAbsenceDays(Math.toIntExact(diff));
    }

    protected boolean cantCalculateDays() {
        VacationScheduleRequest item = getEditedEntity();
        return item.getStartDate() == null || item.getEndDate() == null;
    }


    @Subscribe
    protected void onAfterShow(AfterShowEvent event) {
        VacationScheduleRequest item = getEditedEntity();
        if (item.getStatus() == null || "DRAFT".equals(item.getStatus().getCode())) {

        }

        if (item.getRequestNumber() == null) {
        }

    }


}