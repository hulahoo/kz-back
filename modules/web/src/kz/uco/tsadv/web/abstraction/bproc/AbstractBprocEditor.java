package kz.uco.tsadv.web.abstraction.bproc;

import com.haulmont.addon.bproc.data.Outcome;
import com.haulmont.addon.bproc.data.OutcomesContainer;
import com.haulmont.addon.bproc.entity.ProcessDefinitionData;
import com.haulmont.addon.bproc.entity.ProcessInstanceData;
import com.haulmont.addon.bproc.entity.TaskData;
import com.haulmont.addon.bproc.form.FormData;
import com.haulmont.addon.bproc.service.*;
import com.haulmont.addon.bproc.web.processform.ProcessFormScreens;
import com.haulmont.addon.bproc.web.uicomponent.outcomespanel.OutcomesPanel;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.components.data.table.ContainerTableItems;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.DataComponents;
import com.haulmont.cuba.gui.screen.Screen;
import com.haulmont.cuba.gui.screen.StandardCloseAction;
import com.haulmont.cuba.gui.screen.StandardEditor;
import com.haulmont.cuba.gui.screen.Subscribe;
import com.haulmont.cuba.gui.util.OperationResult;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.base.common.StaticVariable;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.entity.bproc.AbstractBprocRequest;
import kz.uco.tsadv.entity.bproc.ExtTaskData;
import kz.uco.tsadv.modules.administration.UserExt;
import kz.uco.tsadv.service.BprocService;
import kz.uco.tsadv.service.EmployeeNumberService;
import kz.uco.tsadv.web.screens.bpm.processforms.start.StartBprocScreen;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

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
            outcomesPanel.createLayout(taskData, formData.getOutcomes());
            outcomesPanel.setBeforeTaskCompletedPredicate(formOutcome -> {
                boolean equals = OperationResult.Status.SUCCESS.equals(commitChanges().getStatus());
                bprocTaskService.setAssignee(taskData.getId(), userSession.getUser().getId().toString());
                return equals;
            });
            outcomesPanel.setAfterTaskCompletedHandler(formOutcome -> close(new StandardCloseAction("close")));
            procActionButtonHBox.add(outcomesPanel);
        }
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
        startBtn.setAction(new BaseAction("start").withHandler(this::startProcess));
        startBtn.setCaption("Start");
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
        }
        startProcessForm.addAfterCloseListener(afterCloseEvent -> {
            if (afterCloseEvent.getCloseAction().equals(WINDOW_COMMIT_AND_CLOSE_ACTION))
                closeWithDefaultAction();
        });
        startProcessForm.show();
    }

    @SuppressWarnings({"unchecked", "UnstableApiUsage"})
    public Component generateAssignee(ExtTaskData taskData) {
        List<? extends User> assigneeOrCandidates = taskData.getAssigneeOrCandidates();
        if (!CollectionUtils.isEmpty(assigneeOrCandidates)) {
            if (assigneeOrCandidates.size() == 1) {
                Label<String> lbl = uiComponents.create(Label.TYPE_STRING);
                lbl.setValue(((UserExt) assigneeOrCandidates.get(0)).getFullName());
                return lbl;
            } else {
                PopupView popupView = uiComponents.create(PopupView.class);
                popupView.setMinimizedValue(((UserExt) assigneeOrCandidates.get(0)).getFullName() + ", ...");

                CollectionContainer<UserExt> container = dataComponents.createCollectionContainer(UserExt.class);

                container.setItems((Collection<UserExt>) assigneeOrCandidates);

                Table<UserExt> table = uiComponents.create(Table.class);
                table.addGeneratedColumn("fullName", entity -> {
                    Label<String> lbl = uiComponents.create(Label.TYPE_STRING);
                    lbl.setValue(entity.getFullName());
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
    public Component generateOutcome(TaskData taskData) {
        Label<String> label = uiComponents.create(Label.TYPE_STRING);
        if (taskData.getTaskDefinitionKey().equals("initiator")) {
            label.setValue("Start process");
        } else if (taskData.getEndTime() != null) {
            OutcomesContainer outcomesContainer = bprocService.getProcessVariable(processInstanceData.getId(), taskData.getTaskDefinitionKey() + "_result");
            if (!CollectionUtils.isEmpty(outcomesContainer.getOutcomes()))
                for (Outcome outcome : outcomesContainer.getOutcomes()) {
                    if (Objects.equals(outcome.getUser(), taskData.getAssignee())) {
                        label.setValue(outcome.getOutcomeId());
                    }
                }
        }
        return label;
    }

    public String getProcDefinitionKey() {
        return getEditedEntity().getProcessDefinitionKey();
    }
}
