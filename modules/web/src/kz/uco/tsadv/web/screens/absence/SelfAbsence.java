package kz.uco.tsadv.web.screens.absence;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.TabSheet;
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
import kz.uco.tsadv.service.AssignmentService;
import kz.uco.tsadv.service.EmployeeNumberService;

import javax.inject.Inject;
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
    private UserSession userSession;
    @Inject
    private AssignmentService assignmentService;
    @Inject
    private Notifications notifications;
    @Inject
    private Messages messages;
    @Inject
    private MessageBundle messageBundle;
    @Inject
    private DataManager dataManager;
    @Inject
    private Metadata metadata;
    @Inject
    private CommonService commonService;
    @Inject
    private ScreenBuilders screenBuilders;
    @Inject
    protected TabSheet vacationTabSheet;

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
                .view("absenceRequest.view")
                .list();

        if (list != null && !list.isEmpty()) {
            absenceRequest = list.get(0);
        }
        if (absenceRequest == null) {
            absenceRequest = metadata.create(AbsenceRequest.class);
            absenceRequest.setId(UUID.randomUUID());
            absenceRequest.setAssignmentGroup(assignmentGroup);
            absenceRequest.setStatus(commonService.getEntity(DicRequestStatus.class, "DRAFT"));
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

    public void newVacationScheduleButton() {
        VacationScheduleRequest item = dataManager.create(VacationScheduleRequest.class);
        item.setRequestDate(timeSource.currentTimestamp());

        item.setRequestNumber(employeeNumberService.generateNextRequestNumber());
        item.setPersonGroup(dataManager.load(PersonGroupExt.class).query("select e.personGroup " +
                "from tsadv$UserExt e " +
                "where e.id = :uId").parameter("uId", userSession.getUser().getId())
                .view("personGroupExt-view")
                .list().stream().findFirst().orElse(null));
        item.setStatus(commonService.getEntity(DicRequestStatus.class, "DRAFT"));

        screenBuilders.editor(VacationScheduleRequest.class, this).editEntity(item).build().show();
        vacationTabSheet.setSelectedTab("requestsTab");
    }
}