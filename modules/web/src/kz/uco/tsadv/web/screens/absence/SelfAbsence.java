package kz.uco.tsadv.web.screens.absence;

import com.haulmont.cuba.core.entity.contracts.Id;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.builders.EditorBuilder;
import com.haulmont.cuba.gui.components.Action;
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
import kz.uco.tsadv.entity.bproc.AbstractBprocRequest;
import kz.uco.tsadv.mixins.SelfServiceMixin;
import kz.uco.tsadv.modules.administration.TsadvUser;
import kz.uco.tsadv.modules.personal.group.AssignmentGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.Absence;
import kz.uco.tsadv.modules.personal.model.AbsenceRequest;
import kz.uco.tsadv.modules.personal.model.LeavingVacationRequest;
import kz.uco.tsadv.modules.personal.views.AllAbsenceRequest;
import kz.uco.tsadv.service.AssignmentService;
import kz.uco.tsadv.service.EmployeeNumberService;
import kz.uco.tsadv.service.EmployeeService;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Date;
import java.util.List;

@UiController("tsadv$Absence.self.browse")
@UiDescriptor("self-absence-browse.xml")
@LookupComponent("absencesTable")
@LoadDataBeforeShow
public class SelfAbsence extends StandardLookup<Absence>
        implements SelfServiceMixin {
    @Inject
    protected CollectionContainer<Absence> absencesDc;
    @Inject
    protected CollectionLoader<Absence> absencesDl;
    @Inject
    protected CollectionLoader<AllAbsenceRequest> absenceRequestDl;
    @Inject
    protected CollectionLoader<VacationScheduleRequest> vacationScheduleDl;
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
    @Named("absencesTable.newLeavingVacationRequest")
    protected BaseAction absencesTableNewLeavingVacationRequest;
    @Inject
    protected Table<Absence> absencesTable;
    @Inject
    protected Table<VacationScheduleRequest> vacationScheduleTable;
    @Inject
    protected TabSheet tabSheet;
    @Inject
    protected EmployeeService employeeService;

    @Subscribe
    protected void onBeforeShow(BeforeShowEvent event) {
        TsadvUser tsadvUser = (TsadvUser) userSession.getUser();
        tsadvUser = dataManager.reload(tsadvUser, "userExt.edit");
        absenceRequestDl.setQuery("select e from tsadv_AllAbsenceRequest e " +
                " where e.personGroup = :personGroup");
        absenceRequestDl.setParameter("personGroup", tsadvUser.getPersonGroup());
        absenceRequestDl.load();
    }


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

        if (!list.isEmpty()) {
            absenceRequest = list.get(0);
        }

        if (absenceRequest == null) {
            absenceRequest = metadata.create(AbsenceRequest.class);
            absenceRequest.setAssignmentGroup(assignmentGroup);
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

        boolean isEnabled = event.getItem() != null
                && event.getItem().getType() != null
                && event.getItem().getType().getAvailableForLeavingVacation();
        absencesTableNewLeavingVacationRequest.setEnabled(isEnabled);
    }

    public void newVacationScheduleButton() {
        VacationScheduleRequest item = dataManager.create(VacationScheduleRequest.class);
        Date today = timeSource.currentTimestamp();

        item.setStartDate(today);

        item.setPersonGroup(dataManager.load(PersonGroupExt.class).query("select e.personGroup " +
                "from tsadv$UserExt e " +
                "where e.id = :uId").parameter("uId", userSession.getUser().getId())
                .view("personGroupExt-view")
                .list().stream().findFirst().orElse(null));

        screenBuilders.editor(VacationScheduleRequest.class, this)
                .editEntity(item)
                .build()
                .show()
                .addAfterCloseListener(afterCloseEvent -> {
                    if (afterCloseEvent.getCloseAction().equals(WINDOW_COMMIT_AND_CLOSE_ACTION)) {
                        vacationScheduleDl.load();
                    }
                });
    }

    @SuppressWarnings("unchecked")
    public <T extends AbstractBprocRequest> void openRequest1(AllAbsenceRequest absenceRequest, String columnId) {
        Class<T> javaClass = metadata.getClassNN(absenceRequest.getEntityName()).getJavaClass();
        T abstractBprocRequest = dataManager.load(Id.of(absenceRequest.getId(), javaClass)).one();
        EditorBuilder<T> editor = screenBuilders.editor((Class<T>) abstractBprocRequest.getClass(), this);
        if (abstractBprocRequest instanceof AbsenceRequest) editor.withScreenId("tsadv$AbsenceRequestNew.edit");
        editor.editEntity(abstractBprocRequest)
                .show();
    }

    public void openRequest(VacationScheduleRequest vacationScheduleRequest, String columnId) {
        screenBuilders.editor(vacationScheduleTable)
                .editEntity(vacationScheduleRequest)
                .build().show();
    }

    @Subscribe("absencesTable.newLeavingVacationRequest")
    protected void onAbsencesTableNewLeavingVacationRequest(Action.ActionPerformedEvent event) {
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
}