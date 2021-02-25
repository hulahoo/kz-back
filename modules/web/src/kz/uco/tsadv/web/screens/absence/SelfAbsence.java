package kz.uco.tsadv.web.screens.absence;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.TabSheet;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.entity.VacationScheduleRequest;
import kz.uco.tsadv.mixins.SelfServiceMixin;
import kz.uco.tsadv.modules.personal.dictionary.DicRequestStatus;
import kz.uco.tsadv.modules.personal.group.AssignmentGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.Absence;
import kz.uco.tsadv.modules.personal.model.AbsenceRequest;
import kz.uco.tsadv.modules.personal.model.LeavingVacationRequest;
import kz.uco.tsadv.service.AssignmentService;
import kz.uco.tsadv.service.EmployeeNumberService;
import kz.uco.tsadv.service.EmployeeService;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@UiController("tsadv$Absence.self.browse")
@UiDescriptor("self-absence-browse.xml")
@LookupComponent("absencesTable")
@LoadDataBeforeShow
public class SelfAbsence extends StandardLookup<Absence>
        implements SelfServiceMixin {
    @Inject
    protected TimeSource timeSource;
    @Inject
    protected EmployeeNumberService employeeNumberService;
    @Inject
    protected UserSession userSession;
    @Inject
    protected AssignmentService assignmentService;
    @Inject
    protected Notifications notifications;
    @Inject
    protected Messages messages;
    @Inject
    protected MessageBundle messageBundle;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected Metadata metadata;
    @Inject
    protected CommonService commonService;
    @Inject
    protected ScreenBuilders screenBuilders;
    @Inject
    protected TabSheet vacationTabSheet;
    @Inject
    protected CollectionContainer<Absence> absencesDc;
    @Inject
    protected CollectionLoader<Absence> absencesDl;
    @Named("absencesTable.newLeavingVacationRequest")
    protected BaseAction absencesTableNewLeavingVacationRequest;
    @Inject
    protected Table<Absence> absencesTable;
    @Inject
    protected EmployeeService employeeService;

    @Subscribe("addBtn")
    public void onAddBtnClick(Button.ClickEvent event) {
        AssignmentGroupExt assignmentGroup = assignmentService.getAssignmentGroup(
                userSession.getUser().getLogin(), null);

        if (assignmentGroup == null) {
            notifications.create()
                    .withDescription(messageBundle.getMessage("noAssignment"))
                    .show();
            return;
        }

        AbsenceRequest absenceRequest = null;
        List<AbsenceRequest> list = dataManager.load(AbsenceRequest.class)
                .query(" select distinct e from tsadv$AbsenceRequest e " +
                        " where e.assignmentGroup.id = :assignmentGroupId " +
                        "       and e.status.code = 'DRAFT' ")
                .parameter("assignmentGroupId", assignmentGroup.getId())
                .view("absenceRequest.edit")
                .list();

        if (list != null && !list.isEmpty()) {
            absenceRequest = list.get(0);
        }
        absenceRequest = null;
        if (absenceRequest == null) {
            absenceRequest = metadata.create(AbsenceRequest.class);
            absenceRequest.setId(UUID.randomUUID());
            absenceRequest.setAssignmentGroup(assignmentGroup);
            absenceRequest.setStatus(commonService.getEntity(DicRequestStatus.class, "DRAFT"));
            absenceRequest.setPersonGroup(employeeService.getPersonGroupByUserId(userSession.getUser().getId()));
        }

        screenBuilders.editor(AbsenceRequest.class, this)
                .withScreenId("tsadv$AbsenceRequestNew.edit")
                .editEntity(absenceRequest)
                .build()
                .show();
    }

    @Subscribe("balanceBtn")
    public void onBalanceBtnClick(Button.ClickEvent event) {
        screenBuilders.screen(this)
                .withScreenId("tsadv$myAbsenceBalance")
                .build()
                .show();
    }

    @Subscribe(id = "absencesDc", target = Target.DATA_CONTAINER)
    protected void onAbsencesDcItemChange(InstanceContainer.ItemChangeEvent<Absence> event) {

        boolean isEnabled = event.getItem() != null && "MATERNITY".equals(event.getItem().getType().getCode());
        absencesTableNewLeavingVacationRequest.setEnabled(true);
    }

    public void newVacationScheduleButton() {
        VacationScheduleRequest item = dataManager.create(VacationScheduleRequest.class);
        Date today = timeSource.currentTimestamp();

        item.setRequestDate(today);
        item.setStartDate(today);

        item.setRequestNumber(employeeNumberService.generateNextRequestNumber());
        item.setPersonGroup(dataManager.load(PersonGroupExt.class).query("select e.personGroup " +
                "from tsadv$UserExt e " +
                "where e.id = :uId").parameter("uId", userSession.getUser().getId())
                .view("personGroupExt-view")
                .list().stream().findFirst().orElse(null));
        item.setStatus(commonService.getEntity(DicRequestStatus.class, "DRAFT"));

        screenBuilders.editor(VacationScheduleRequest.class, this)
                .editEntity(item)
                .build()
                .show()
                .addAfterCloseListener(afterCloseEvent -> {
//                    vacationScheduleRequestDl.load();
                    vacationTabSheet.setSelectedTab("requestsTab");
                });
    }

    public void newLeavingVacationRequest() {
        Absence getItem = absencesDc.getItem();
        LeavingVacationRequest item = dataManager.create(LeavingVacationRequest.class);

        item.setVacation(getItem);
        item.setStartDate(getItem.getDateFrom());
        item.setEndDate(getItem.getDateTo());

        screenBuilders.editor(LeavingVacationRequest.class, this)
                .newEntity(item)
                .build()
                .show();
    }

    public void openRequest(AbsenceRequest absenceRequest, String columnId) {
        screenBuilders.editor(AbsenceRequest.class, this)
                .withScreenId("tsadv$AbsenceRequestNew.edit")
                .editEntity(absenceRequest)
                .show();
    }
}