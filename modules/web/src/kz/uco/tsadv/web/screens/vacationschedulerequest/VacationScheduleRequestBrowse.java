package kz.uco.tsadv.web.screens.vacationschedulerequest;

import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.Screens;
import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.screen.LookupComponent;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.entity.VacationScheduleRequest;
import kz.uco.tsadv.global.enums.SendingToOracleStatus;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.service.AssignmentService;

import javax.inject.Inject;


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

    @Inject
    protected Button sendToOracleBtn;
    @Inject
    private DataGrid<VacationScheduleRequest> vacationScheduleRequestsTable;

    @Subscribe("sendToOracleBtn")
    protected void onSendToOracleBtnClick(Button.ClickEvent event) {
        CommitContext commitContext = new CommitContext();
        vacationScheduleRequestsTable.getSelected().forEach(vacationScheduleRequest -> {
            if (!SendingToOracleStatus.SENDING_TO_ORACLE.equals(vacationScheduleRequest.getSentToOracle())) {
                vacationScheduleRequest.setSentToOracle(SendingToOracleStatus.SENDING_TO_ORACLE);
                commitContext.addInstanceToCommit(vacationScheduleRequest);
            }
        });
        dataManager.commit(commitContext);
        vacationScheduleRequestsDl.load();
        vacationSchedulesDl.load();
        tabSheet.setSelectedTab("vacationScheduleTab");
    }

    public void openPersonCard(VacationScheduleRequest request, String columnId) {
        PersonGroupExt personGroup = request.getPersonGroup();

//        AssignmentExt assignment = assignmentService.getAssignment(personGroup.getId(), View.MINIMAL);

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

    @Install(to = "vacationScheduleRequestsTable.requestNumber", subject = "columnGenerator")
    private Component vacationScheduleRequestsTableRequestNumberColumnGenerator(DataGrid.ColumnGeneratorEvent<VacationScheduleRequest> event) {
        LinkButton linkButton = uiComponents.create(LinkButton.class);
        linkButton.setCaption(event.getItem().getRequestNumber().toString());
        linkButton.setAction(new BaseAction("requestNumber").withHandler(e -> {
            screenBuilders.editor(vacationScheduleRequestsTable)
                    .editEntity(event.getItem())
                    .build().show();
        }));
        return linkButton;
    }

    @Install(to = "vacationScheduleRequestsTable.personGroup", subject = "columnGenerator")
    private Component vacationScheduleRequestsTablePersonGroupFioWithEmployeeNumberColumnGenerator(DataGrid.ColumnGeneratorEvent<VacationScheduleRequest> event) {
        PersonGroupExt personGroup = event.getItem().getPersonGroup();

        LinkButton linkButton = uiComponents.create(LinkButton.class);
        linkButton.setCaption(event.getItem().getPersonGroup().getFioWithEmployeeNumber());
        linkButton.setAction(new BaseAction("personGroup").withHandler(e ->
                screenBuilders.editor(PersonGroupExt.class, this)
                        .editEntity(personGroup)
                        .withScreenId("person-card")
                        .build()
                        .show()));
        return linkButton;
    }
}