package kz.uco.tsadv.web.assignedevent;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.GlobalConfig;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.entity.tb.AssignedEvent;
import kz.uco.tsadv.modules.administration.UserExt;
import kz.uco.tsadv.service.EmployeeService;
import kz.uco.uactivity.entity.Activity;
import kz.uco.uactivity.entity.ActivityType;
import kz.uco.uactivity.entity.Priority;
import kz.uco.uactivity.entity.StatusEnum;
import kz.uco.uactivity.service.ActivityService;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Calendar;
import java.util.*;

public class AssignedEventBrowse extends AbstractLookup {

    @Named("assignedEventsTable.create")
    private CreateAction createAction;
    @Named("assignedEventsTable.edit")
    private EditAction editAction;
    @Inject
    private UserSession userSession;
    @Inject
    private CommonService commonService;
    @Named("assignedEventsTable.remove")
    private RemoveAction assignedEventsTableRemove;
    @Inject
    private GroupDatasource<AssignedEvent, UUID> assignedEventsIgnoringSafetyDs;
    @Inject
    private DataManager dataManager;
    @Inject
    private PopupButton duplicate;
    @Inject
    private Metadata metadata;
    @Inject
    private ActivityService activityService;
    @Inject
    private EmployeeService employeeService;
    @Inject
    private GlobalConfig globalConfig;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        createAction.setAfterCommitHandler(entity -> assignedEventsIgnoringSafetyDs.refresh());
        editAction.setAfterCommitHandler(entity -> assignedEventsIgnoringSafetyDs.refresh());

        Map<String, Object> map = new HashMap<>();
        assignedEventsTableRemove.setBeforeActionPerformedHandler(() -> {
            map.put("assignedEventId", assignedEventsIgnoringSafetyDs.getItem().getId());
            Activity activity = commonService.getEntity(Activity.class,
                    "select e from uactivity$Activity e where e.referenceId = :assignedEventId",
                    map,
                    "activity-view");
            if (activity != null) {
                dataManager.remove(activity);
            }
            return true;
        });
        duplicate.setEnabled(false);
        assignedEventsIgnoringSafetyDs.addItemChangeListener(e -> {
            if (e.getItem() != null && e.getItem().getCreatedBy() != null) {
                if (e.getItem().getCreatedBy().equals(userSession.getUser().getLogin())) {
                    assignedEventsTableRemove.setVisible(true);
                } else {
                    assignedEventsTableRemove.setVisible(false);
                }
            }
            if (e.getItem() == null) {
                duplicate.setEnabled(false);
            } else {
                duplicate.setEnabled(true);
            }
        });
    }

    public void duplicateByMonth() {
        showConfirmDialog(1);
    }

    public void duplicateByQuarter() {
        showConfirmDialog(3);
    }

    public void duplicateByHalfYear() {
        showConfirmDialog(6);
    }

    private void duplicate(int step) {
        AssignedEvent assignedEventDb = assignedEventsIgnoringSafetyDs.getItem();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(assignedEventsIgnoringSafetyDs.getItem().getDeadline());
        int month = calendar.get(Calendar.MONTH);
        List<Entity> instancesToCommit = new ArrayList<>();
        for (int i = step - 1; i < 12; i += step) {
            if (i != month) {
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                calendar.set(Calendar.MONTH, i);
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));

                AssignedEvent assignedEvent = metadata.create(AssignedEvent.class);
                assignedEvent.setAssigned(assignedEventDb.getAssigned());
                assignedEvent.setAssignment(assignedEventDb.getAssignment());
                assignedEvent.setDeadline(calendar.getTime());
                assignedEvent.setBudgetFact(assignedEventDb.getBudgetFact());
                assignedEvent.setFact(assignedEventDb.getFact());
                assignedEvent.setBudgetPlan(assignedEventDb.getBudgetPlan());
                assignedEvent.setInvestmentFact(assignedEventDb.getInvestmentFact());
                assignedEvent.setInvestmentPlan(assignedEventDb.getInvestmentPlan());
                assignedEvent.setParentEvent(assignedEventDb.getParentEvent());
                assignedEvent.setSafetyEvent(assignedEventDb.getSafetyEvent());
                assignedEvent.setSafetyPlanEvent(assignedEventDb.getSafetyPlanEvent());
                assignedEvent.setStatus(assignedEventDb.getStatus());

                UserExt assignedBy = employeeService.getUserExtByPersonGroupId(assignedEvent.getAssigned() == null ? null : assignedEvent.getAssigned().getId());
                activityService.createActivity(
                        assignedEvent.getSafetyEvent().getName(),
                        assignedEvent.getSafetyEvent().getName(),
                        assignedEvent.getSafetyEvent().getName(),
                        employeeService.getUserExtByPersonGroupId(assignedEvent.getAssignment() == null ? null : assignedEvent.getAssignment().getId()),
                        assignedBy != null ? assignedBy : employeeService.getSystemUser(),
                        commonService.getEntity(ActivityType.class, "tsadv$AssignedEvent"),
                        StatusEnum.active,
                        null,
                        assignedEvent.getDeadline(),
                        assignedEvent.getDeadline(),
                        commonService.getEntity(Priority.class, "MEDIUM"),
                        globalConfig.getWebAppUrl() + "/open?screen=tsadv$AssignedEvent.edit&item=tsadv$AssignedEvent-" + assignedEvent.getId(),
                        assignedEvent.getId(),
                        null);

                instancesToCommit.add(assignedEvent);
            }
        }
        dataManager.commit(new CommitContext(instancesToCommit));
        assignedEventsIgnoringSafetyDs.refresh();
    }

    private void showConfirmDialog(int step) {
        showOptionDialog(getMessage("duplicate.approve.title"),
                getMessage("duplicate.approve.body"),
                MessageType.CONFIRMATION,
                new Action[]{
                        new DialogAction(DialogAction.Type.YES) {
                            @Override
                            public void actionPerform(Component component) {
                                duplicate(step);
                            }
                        },
                        new DialogAction(DialogAction.Type.NO)
                });
    }
}