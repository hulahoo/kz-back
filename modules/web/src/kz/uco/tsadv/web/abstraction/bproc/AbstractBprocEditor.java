package kz.uco.tsadv.web.abstraction.bproc;

import com.haulmont.addon.bproc.entity.ProcessDefinitionData;
import com.haulmont.addon.bproc.entity.ProcessInstanceData;
import com.haulmont.addon.bproc.entity.TaskData;
import com.haulmont.addon.bproc.form.FormData;
import com.haulmont.addon.bproc.form.FormOutcome;
import com.haulmont.addon.bproc.service.*;
import com.haulmont.addon.bproc.web.processform.ProcessFormScreens;
import com.haulmont.addon.bproc.web.uicomponent.outcomespanel.OutcomesPanel;
import com.haulmont.cuba.core.entity.contracts.Id;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.Screens;
import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.components.data.table.ContainerTableItems;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.DataComponents;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.gui.util.OperationResult;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.base.common.StaticVariable;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.components.GroupsComponent;
import kz.uco.tsadv.entity.bproc.AbstractBprocRequest;
import kz.uco.tsadv.entity.bproc.ExtTaskData;
import kz.uco.tsadv.modules.administration.TsadvUser;
import kz.uco.tsadv.service.BprocService;
import kz.uco.tsadv.service.EmployeeNumberService;
import kz.uco.tsadv.web.screens.bpm.processforms.outcome.BprocOutcomeDialog;
import kz.uco.tsadv.web.screens.bpm.processforms.start.StartBprocScreen;
import kz.uco.uactivity.entity.Activity;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.*;
import java.util.function.Supplier;

/**
 * @author Alibek Berdaulet
 */
public abstract class AbstractBprocEditor<T extends AbstractBprocRequest> extends StandardEditor<T> {

    @Inject
    protected CollectionContainer<ExtTaskData> tasksDc;

    @Inject
    protected ProcessFormScreens processFormScreens;
    @Inject
    protected BprocRepositoryService bprocRepositoryService;
    @Inject
    protected BprocRuntimeService bprocRuntimeService;
    @Inject
    protected BprocTaskService bprocTaskService;
    @Inject
    protected BprocFormService bprocFormService;
    @Inject
    protected BprocHistoricService bprocHistoricService;
    @Inject
    protected BprocService bprocService;

    @Inject
    protected DataComponents dataComponents;
    @Inject
    protected Metadata metadata;
    @Inject
    protected UiComponents uiComponents;
    @Inject
    protected UserSession userSession;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected Messages messages;
    @Inject
    protected EmployeeNumberService employeeNumberService;
    @Inject
    protected CommonService commonService;
    @Inject
    protected Notifications notifications;
    @Inject
    protected Screens screens;
    @Inject
    protected GroupsComponent groupsComponent;

    @WindowParam
    protected String activityId;

    @Inject
    protected HBoxLayout procActionButtonHBox;
    @Inject
    protected VBoxLayout bpmActorsVBox;

    protected TaskData activeTaskData;
    protected ProcessInstanceData processInstanceData;

    @Subscribe
    protected void onAfterShow(AfterShowEvent event) {
        initVariables();
        initFields();
        initVisibleFields();
        initEditableFields();
    }

    protected void initVariables() {
        if (activityId != null) {
            Activity activity = dataManager.load(Id.of(UUID.fromString(activityId), Activity.class))
                    .view("activity.view.tsadv")
                    .one();

            groupsComponent.addActivityHeaderToScreen(this, activity);
        }
        processInstanceData = getProcessInstanceData();
        fillTasksDc();
        activeTaskData = getActiveTaskData();
    }

    protected void initFields() {
        if (!isProcessStarted()) initStartBtn();

        List<TaskData> tasks = bprocTaskService.createTaskDataQuery()
                .processInstanceBusinessKey(getEditedEntity().getId().toString())
                .taskCandidateOrAssigned(userSession.getUser())
                .list();

        if (!tasks.isEmpty()) {
            TaskData taskData = tasks.get(0);
            FormData formData = bprocFormService.getTaskFormData(taskData.getId());

            Assert.notNull(formData, "FormData not found!");

            OutcomesPanel outcomesPanel = uiComponents.create(OutcomesPanel.class);
            List<FormOutcome> outcomes = filterOutcomes(formData);

            //adding reassign
//            FormOutcome reassign = new FormOutcome();
//            reassign.setId(AbstractBprocRequest.OUTCOME_REASSIGN);
//            outcomes.stream()
//                    .filter(formOutcome -> formOutcome.getId().equals(AbstractBprocRequest.OUTCOME_REVISION))
//                    .findAny().
//                    ifPresent(revision -> outcomes.add(outcomes.indexOf(revision) + 1, reassign));

            outcomesPanel.createLayout(taskData, outcomes);
            outcomesPanel.setBeforeTaskCompletedPredicate(formOutcome -> {
                boolean equals = OperationResult.Status.SUCCESS.equals(commitChanges().getStatus());
                bprocTaskService.setAssignee(taskData.getId(), userSession.getUser().getId().toString());
                return equals;
            });
            outcomesPanel.setAfterTaskCompletedHandler(formOutcome -> {
                notifications.create(Notifications.NotificationType.HUMANIZED)
                        .withPosition(Notifications.Position.MIDDLE_CENTER)
                        .withDescription(messages.getMainMessage("user.answer." + formOutcome.getId()))
                        .show();
                close(new StandardCloseAction("close"));
            });

            //override captions
            List<Action> actions = outcomesPanel.getActions();
            actions.remove(new BaseAction(AbstractBprocRequest.OUTCOME_REASSIGN));
            actions.forEach(action -> action.setCaption(messages.getMainMessage("OUTCOME_" + action.getId())));
            procActionButtonHBox.add(outcomesPanel);

            //override reassign action
            for (Component component : outcomesPanel.getLayout().getComponents()) {
                if (component instanceof Button) {
                    Button button = (Button) component;

                    Action action = button.getAction();
//                    noinspection ConstantConditions
                    button.setAction(new BaseAction(action.getId() + "_DIALOG")
                                    .withCaption(action.getCaption())
                                    .withHandler(event -> {
                                        BprocOutcomeDialog outcomeDialog = (BprocOutcomeDialog) screens.create("tsadv_BprocOutcomeDialog", OpenMode.DIALOG);
                                        String outcome = action.getId();

                                        Supplier<TsadvUser> userSupplier = outcomeDialog.getUserSupplier();
                                        outcomesPanel.setProcessVariablesSupplier(() -> {
                                            Map<String, Object> params = new HashMap<>();
                                            params.put("comment", outcomeDialog.getComment());
                                            return params;
                                        });

                                        //noinspection ConstantConditions
                                        outcomeDialog.setCommentRequired(isCommentRequired(outcome));
                                        outcomeDialog.setOutcome(outcome);
                                        if (!AbstractBprocRequest.OUTCOME_REASSIGN.equals(action.getId()))
                                            outcomeDialog.setAction(action);
                                        else outcomeDialog.setAction(new BaseAction(AbstractBprocRequest.OUTCOME_REASSIGN)
                                                .withCaption(messages.getMainMessage("OUTCOME_" + AbstractBprocRequest.OUTCOME_REASSIGN))
                                                .withHandler(e -> {
                                                    bprocTaskService.setAssignee(taskData.getId(), userSupplier.get().getId().toString());
//                                            if (outcomesPanel.getAfterTaskCompletedHandler() != null)
//                                                outcomesPanel.getAfterTaskCompletedHandler().accept(reassign);
                                                }));
                                        outcomeDialog.setAction(action);
                                        outcomeDialog.setOutcomesPanel(outcomesPanel);

                                        outcomeDialog.show();
                                    })
                    );
                }
            }
        }
    }

    protected List<FormOutcome> filterOutcomes(FormData formData) {
        List<FormOutcome> list = new ArrayList<>();
        for (FormOutcome outcome : formData.getOutcomes()) {
            if (!isHiddenOutcome(outcome)) list.add(outcome);
        }
        return list;
    }

    protected boolean isHiddenOutcome(FormOutcome outcome) {
        return false;
    }

    protected boolean isCommentRequired(String outcome) {
        return !outcome.equals(AbstractBprocRequest.OUTCOME_APPROVE)
                && !outcome.equals(AbstractBprocRequest.OUTCOME_SEND_FOR_APPROVAL);
    }

    protected void initEditableFields() {

    }

    protected void initVisibleFields() {
        bpmActorsVBox.setVisible(!tasksDc.getItems().isEmpty());
    }

    protected boolean isProcessStarted() {
        return processInstanceData != null && processInstanceData.getStartTime() != null;
    }

    protected void initStartBtn() {
        Button startBtn = uiComponents.create(Button.class);
        startBtn.setAction(new BaseAction("START")
                .withCaption(messages.getMainMessage("OUTCOME_START"))
                .withHandler(this::startProcess));
        procActionButtonHBox.add(startBtn);
    }

    protected ProcessInstanceData getProcessInstanceData() {
        return bprocService.getProcessInstanceData(getEditedEntity().getId().toString(), getProcDefinitionKey());
    }

    protected void fillTasksDc() {
        if (processInstanceData != null) tasksDc.setItems(bprocService.getProcessTasks(processInstanceData));
    }

    protected TaskData getActiveTaskData() {
        return tasksDc.getItems().stream()
                .filter(taskData -> taskData.getEndTime() == null)
                .findAny()
                .orElse(null);
    }

    protected boolean isDraft() {
        return hasStatus("DRAFT");
    }

    protected boolean hasStatus(String requestStatus) {
        T editedEntity = getEditedEntity();
        return editedEntity.getStatus() != null && requestStatus.equals(editedEntity.getStatus().getCode());
    }

    protected void startProcess(Action.ActionPerformedEvent event) {
        if (!OperationResult.Status.SUCCESS.equals(commitChanges().getStatus())) return;

        ProcessDefinitionData processDefinitionData = bprocRepositoryService
                .createProcessDefinitionDataQuery()
                .processDefinitionKey(getProcDefinitionKey())
                .active()
                .latestVersion()
                .singleResult();

        Screen startProcessForm = this.processFormScreens.createStartProcessForm(processDefinitionData, this);
        if (startProcessForm instanceof StartBprocScreen) {
            ((StartBprocScreen) startProcessForm).setEntity(getEditedEntity());
            ((StartBprocScreen) startProcessForm).setPersonGroupId(userSession.getAttribute(StaticVariable.USER_PERSON_GROUP_ID));
            ((StartBprocScreen) startProcessForm).setEmployee(getEmployee());
            ((StartBprocScreen) startProcessForm).setProcessVariableSupplier(this::getProcessVariables);
        }
        startProcessForm.addAfterCloseListener(afterCloseEvent -> {
            if (afterCloseEvent.getCloseAction().equals(WINDOW_COMMIT_AND_CLOSE_ACTION))
                closeWithDefaultAction();
        });
        startProcessForm.show();
    }

    @Nullable
    protected TsadvUser getEmployee() {
        return null;
    }

    @Nullable
    protected Map<String, Object> getProcessVariables() {
        return null;
    }

    @SuppressWarnings({"unchecked", "UnstableApiUsage"})
    public Component generateAssignee(ExtTaskData taskData) {
        List<TsadvUser> assigneeOrCandidates = taskData.getAssigneeOrCandidates();
        if (!CollectionUtils.isEmpty(assigneeOrCandidates)) {
            if (assigneeOrCandidates.size() == 1) {
                Label<String> lbl = uiComponents.create(Label.TYPE_STRING);
                lbl.setValue(assigneeOrCandidates.get(0).getFullNameWithLogin());
                return lbl;
            } else {
                PopupView popupView = uiComponents.create(PopupView.class);
                popupView.setMinimizedValue(assigneeOrCandidates.get(0).getFullNameWithLogin() + " +" + (assigneeOrCandidates.size() - 1));

                CollectionContainer<TsadvUser> container = dataComponents.createCollectionContainer(TsadvUser.class);

                container.setItems(assigneeOrCandidates);

                Table<TsadvUser> table = uiComponents.create(Table.class);
                table.addGeneratedColumn("fullNameWithLogin", entity -> {
                    Label<String> lbl = uiComponents.create(Label.TYPE_STRING);
                    lbl.setValue(entity.getFullNameWithLogin());
                    return lbl;
                });
                table.setItems(new ContainerTableItems<>(container));
                table.setColumnHeaderVisible(false);
                table.setShowSelection(false);
                table.setWidthAuto();

                popupView.setPopupContent(table);
                return popupView;
            }
        }
        return null;
    }


    @SuppressWarnings("UnstableApiUsage")
    public Component generateOutcome(ExtTaskData taskData) {
        Label<String> label = uiComponents.create(Label.TYPE_STRING);
        String outcome = taskData.getOutcome();
        if (outcome != null)
            label.setValue(messages.getMainMessage("OUTCOME_" + outcome));
        return label;
    }

    public String getProcDefinitionKey() {
        return getEditedEntity().getProcessDefinitionKey();
    }
}
