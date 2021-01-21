package kz.uco.tsadv.mixins;

import com.haulmont.addon.bproc.entity.ProcessDefinitionData;
import com.haulmont.addon.bproc.entity.ProcessInstanceData;
import com.haulmont.addon.bproc.entity.TaskData;
import com.haulmont.addon.bproc.form.FormData;
import com.haulmont.addon.bproc.form.FormOutcome;
import com.haulmont.addon.bproc.form.FormParam;
import com.haulmont.addon.bproc.service.BprocFormService;
import com.haulmont.addon.bproc.service.BprocRepositoryService;
import com.haulmont.addon.bproc.service.BprocRuntimeService;
import com.haulmont.addon.bproc.service.BprocTaskService;
import com.haulmont.addon.bproc.web.processform.screencreator.ProcessFormScreenCreator;
import com.haulmont.addon.bproc.web.processform.screencreator.ProcessFormScreenCreatorsBean;
import com.haulmont.addon.bproc.web.uicomponent.outcomespanel.OutcomesPanel;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.BeanLocator;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.HBoxLayout;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.components.data.table.ContainerTableItems;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.model.DataComponents;
import com.haulmont.cuba.gui.model.DataContext;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.tsadv.entity.bproc.AbstractBprocRequest;
import kz.uco.tsadv.entity.bproc.BprocTaskHistory;
import kz.uco.tsadv.modules.personal.dictionary.DicRequestStatus;
import kz.uco.tsadv.service.BprocUtilService;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public interface BprocActionMixin<T extends AbstractBprocRequest> extends EditorScreen<T> {
    @Subscribe
    default void onBprocActionMixinBeforeShow(Screen.BeforeShowEvent event) {
        BeanLocator beanLocator = Extensions.getBeanLocator(event.getSource());
        UiComponents uiComponents = beanLocator.get(UiComponents.class);
        Messages messages = beanLocator.get(Messages.class);
        BprocRuntimeService bprocRuntimeService = beanLocator.get(BprocRuntimeService.class);

        Screen screen = event.getSource();
        ProcessInstanceData processInstanceData = bprocRuntimeService.createProcessInstanceDataQuery()
                .processDefinitionKey(getProcDefinitionKey())
                .variableValueEquals("entityId", getEditedEntity().getId().toString())
                .list().stream().findFirst().orElse(null);
        BprocFormService bprocFormService = beanLocator.get(BprocFormService.class);
        if (processInstanceData != null) {
            UserSession userSession = beanLocator.get(UserSession.class);
            BprocTaskService bprocTaskService = beanLocator.get(BprocTaskService.class);
            List<TaskData> tasks = bprocTaskService.createTaskDataQuery()
                    .processDefinitionKey(getProcDefinitionKey())
                    .taskAssignee(userSession.getCurrentOrSubstitutedUser())
                    .processVariableValueEquals("entityId", getEditedEntity().getId().toString())
                    .orderByTaskCreateTime()
                    .desc()
                    .list();
            if (!tasks.isEmpty()) {
                TaskData taskData = tasks.get(0);
                FormData formData = bprocFormService.getTaskFormData(taskData.getId());
                if (formData == null) {
                    throw new NullPointerException("npe");
                }
                OutcomesPanel outcomesPanel = uiComponents.create(OutcomesPanel.class);
                outcomesPanel.createLayout(taskData, formData.getOutcomes());
                outcomesPanel.setAfterTaskCompletedHandler(formOutcome -> screen.close(new StandardCloseAction("close")));
                ((HBoxLayout) screen.getWindow().getComponentNN("procActionButtonHBox")).add(outcomesPanel);
            }

            DataComponents dataComponents = beanLocator.get(DataComponents.class);
            DataContext dataContext = dataComponents.createDataContext();
            UiControllerUtils.getScreenData(screen).setDataContext(dataContext);

            CollectionContainer<BprocTaskHistory> bprocTaskHistoryDc = dataComponents.createCollectionContainer(BprocTaskHistory.class);

            CollectionLoader<BprocTaskHistory> bprocTaskHistoryDl = dataComponents.createCollectionLoader();
            bprocTaskHistoryDl.setContainer(bprocTaskHistoryDc);
            bprocTaskHistoryDl.setDataContext(dataContext);
            bprocTaskHistoryDc.getMutableItems().addAll(beanLocator.get(BprocUtilService.class).getBprocTaskHistory(processInstanceData.getId()));

            Table<BprocTaskHistory> table = uiComponents.create(Table.of(BprocTaskHistory.class));
            table.setItems(new ContainerTableItems<>(bprocTaskHistoryDc));
            HBoxLayout procApproversBox = ((HBoxLayout) screen.getWindow().getComponentNN("procApproversBox"));
            procApproversBox.setWidthFull();
            procApproversBox.add(table);
        } else {
            Button button = uiComponents.create(Button.class);
            button.setId("startProcess");
            button.setCaption(messages.getMainMessage("bproc.start.process"));
            button.setAction(new BaseAction("startProcess").withHandler(actionPerformedEvent -> {
                startProcess(beanLocator, screen, bprocFormService);
            }));
            ((HBoxLayout) screen.getWindow().getComponentNN("procActionButtonHBox")).add(button);

        }
    }

    default void startProcess(BeanLocator beanLocator, Screen screen, BprocFormService bprocFormService) {
        if (!beforeOpenRunProcessDialogHandler()) return;

        ProcessDefinitionData latestProcessDefinition = this.findLatestProcessDefinition(getProcDefinitionKey(), beanLocator.get(BprocRepositoryService.class));
        ProcessFormScreenCreatorsBean processFormScreenCreatorsBean = beanLocator.get(ProcessFormScreenCreatorsBean.class);
        FormData formData = bprocFormService.getStartFormData(latestProcessDefinition.getId());
        String formType = formData != null ? formData.getType() : null;
        FormParam formParam = new FormParam();
        formParam.setName("entityName");
        formParam.setValue(getEditedEntity().getMetaClass().getName());
        List<FormParam> list = new ArrayList<>();
        list.add(formParam);
        formData.setFormParams(list);
        formData.setBusinessKey(getEditedEntity().getId().toString());
        ProcessFormScreenCreator formCreator = formType != null ? processFormScreenCreatorsBean.getScreenCreator(formType) : processFormScreenCreatorsBean.getDefaultScreenCreator();
        if (formCreator != null) {
            ProcessFormScreenCreator.CreationContext creationContext = new ProcessFormScreenCreator.CreationContext(latestProcessDefinition, formData,
                    (FrameOwner) this);
            formCreator.createStartProcessScreen(creationContext).show()
                    .addAfterCloseListener(afterCloseEvent -> {
//                        getEditedEntity().setStatus(beanLocator.get(DataManager.class).load(DicRequestStatus.class)
//                                .query("select e from tsadv$DicRequestStatus e" +
//                                        " where e.code = 'APPROVING'").one());
                        afterCloseRunProcessDialogHandler(screen, afterCloseEvent);
                    });
        } else {
            throw new RuntimeException("Unsupported form type " + formType);
        }
    }


    default Button createOutcomeBtn(FormOutcome outcome, UiComponents uiComponents, Messages messages) {
        Button button = uiComponents.create(Button.class);
//        BaseAction outcomeBtnAction = new BaseAction(outcome.getId())
//                //todo MG add outcome caption?
//                .withCaption(messages.getMainMessage("bproc.outcome." + outcome.getId()))
//                .withHandler(actionPerformedEvent -> {
//                    if (beforeTaskCompletedPredicate != null && !beforeTaskCompletedPredicate.test(outcome))
//                        return;
//                    Map<String, Object> processVariables = processVariablesSupplier != null ?
//                            processVariablesSupplier.get() :
//                            new HashMap<>();
//                    bprocTaskService.completeWithOutcome(taskData,
//                            outcome.getId(),
//                            processVariables);
//                    if (afterTaskCompletedHandler != null)
//                        afterTaskCompletedHandler.accept(outcome);
//                });
//        button.setAction(outcomeBtnAction);
//        if (!Strings.isNullOrEmpty(buttonsWidth))
//            button.setWidth(buttonsWidth);
//        actions.add(outcomeBtnAction);
        return button;
    }

    default boolean beforeOpenRunProcessDialogHandler() {
        return false;
    }

    default void afterCloseRunProcessDialogHandler(Screen screen, Screen.AfterCloseEvent afterCloseEvent) {

    }

    @Nullable
    default ProcessDefinitionData findLatestProcessDefinition(String processDefinitionKey,
                                                              BprocRepositoryService bprocRepositoryService) {
        List<ProcessDefinitionData> list = bprocRepositoryService.createProcessDefinitionDataQuery().processDefinitionKey(processDefinitionKey).latestVersion().active().list();
        return !list.isEmpty() ? (ProcessDefinitionData) list.get(0) : null;
    }

    String getProcDefinitionKey();
}
