package kz.uco.tsadv.web.screens.vacationschedulerequest;

import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.Screens;
import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.screen.LookupComponent;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.entity.VacationScheduleRequest;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.service.AssignmentService;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;


/**
 * @author Alibek Berdaulet
 */
@UiController("tsadv_VacationScheduleRequest.browse")
@UiDescriptor("vacation-schedule-request-browse.xml")
@LookupComponent("vacationScheduleRequestsTable")
@LoadDataBeforeShow
public class VacationScheduleRequestBrowse extends StandardLookup<VacationScheduleRequest> {

    @Inject
    protected CollectionLoader<VacationScheduleRequest> vacationSchedulesDl;
    @Inject
    protected CollectionLoader<VacationScheduleRequest> vacationScheduleRequestsDl;
    @Inject
    protected UiComponents uiComponents;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected CommonService commonService;
    @Inject
    protected Metadata metadata;
    @Inject
    protected Screens screens;
    @Inject
    protected ScreenBuilders screenBuilders;
    @Inject
    protected AssignmentService assignmentService;
    @Inject
    protected TabSheet tabSheet;

    protected Set<VacationScheduleRequest> selectedVacationScheduleRequests = new HashSet<>();

    public Component checkListGenerator(VacationScheduleRequest vacationScheduleRequest) {
        CheckBox checkBox = uiComponents.create(CheckBox.NAME);
        checkBox.setValue(selectedVacationScheduleRequests.contains(vacationScheduleRequest));
        checkBox.addValueChangeListener(e -> this.checkListChangedListener(e, vacationScheduleRequest));
        return checkBox;
    }

    protected void checkListChangedListener(HasValue.ValueChangeEvent<Boolean> event, VacationScheduleRequest vacationScheduleRequest) {
        if (Boolean.TRUE.equals(event.getValue())) {
            selectedVacationScheduleRequests.add(vacationScheduleRequest);
        } else selectedVacationScheduleRequests.remove(vacationScheduleRequest);
    }

    @Subscribe("sendToOracleBtn")
    protected void onSendToOracleBtnClick(Button.ClickEvent event) {
        CommitContext commitContext = new CommitContext();

        selectedVacationScheduleRequests.stream()
                .peek(vacationScheduleRequest -> vacationScheduleRequest.setSentToOracle(true))
//                .peek(commitContext::addInstanceToCommit)
//                .map(this::createVacationSchedule)
                .forEach(commitContext::addInstanceToCommit);

        dataManager.commit(commitContext);

        selectedVacationScheduleRequests.clear();
        vacationScheduleRequestsDl.load();
        vacationSchedulesDl.load();
        tabSheet.setSelectedTab("vacationScheduleTab");
    }

   /* protected VacationSchedule createVacationSchedule(VacationScheduleRequest scheduleRequest) {
        VacationSchedule vacationSchedule = metadata.create(VacationSchedule.class);
        vacationSchedule.setAbsenceDays(scheduleRequest.getAbsenceDays());
        vacationSchedule.setStartDate(scheduleRequest.getStartDate());
        vacationSchedule.setEndDate(scheduleRequest.getEndDate());
        vacationSchedule.setPersonGroup(scheduleRequest.getPersonGroup());
        vacationSchedule.setRequest(scheduleRequest);
        return vacationSchedule;
    }*/

    public void openPersonCard(VacationScheduleRequest request, String columnId) {
        PersonGroupExt personGroup = request.getPersonGroup();

        AssignmentExt assignment = assignmentService.getAssignment(personGroup.getId(), View.MINIMAL);

        screenBuilders.editor(PersonGroupExt.class, this)
                .editEntity(personGroup)
                .withScreenId("person-card")
                .build()
                .show();
    }

    public void openRequest(VacationScheduleRequest request, String columnId) {
        screenBuilders.editor(VacationScheduleRequest.class, this)
                .editEntity(request)
                .build()
                .show()
                .addAfterCloseListener(afterCloseEvent -> vacationScheduleRequestsDl.load());
    }
}