package kz.uco.tsadv.mixins;

import com.haulmont.addon.bproc.entity.ProcessDefinitionData;
import com.haulmont.addon.bproc.entity.ProcessInstanceData;
import com.haulmont.addon.bproc.entity.TaskData;
import com.haulmont.addon.bproc.form.FormData;
import com.haulmont.addon.bproc.service.BprocFormService;
import com.haulmont.addon.bproc.service.BprocRepositoryService;
import com.haulmont.addon.bproc.service.BprocRuntimeService;
import com.haulmont.addon.bproc.service.BprocTaskService;
import com.haulmont.addon.bproc.web.processform.screencreator.ProcessFormScreenCreator;
import com.haulmont.addon.bproc.web.processform.screencreator.ProcessFormScreenCreatorsBean;
import com.haulmont.addon.bproc.web.uicomponent.outcomespanel.OutcomesPanel;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.BeanLocator;
import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.HBoxLayout;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.security.global.UserSession;

import javax.annotation.Nullable;
import java.util.List;

public interface BprocActionMixin<T extends Entity> extends EditorScreen<T> {
    @Subscribe
    default void onBprocActionMixinBeforeShow(Screen.BeforeShowEvent event) {
        BeanLocator beanLocator = Extensions.getBeanLocator(event.getSource());
        UiComponents uiComponents = beanLocator.get(UiComponents.class);
        BprocRuntimeService bprocRuntimeService = beanLocator.get(BprocRuntimeService.class);


        Screen screen = event.getSource();
        ProcessInstanceData processInstanceData = bprocRuntimeService.createProcessInstanceDataQuery()
                .processDefinitionKey(getProcDefinitionKey())
                .variableValueEquals("entityId", getEditedEntity().getId())
                .list().stream().findFirst().orElse(null);
        BprocFormService bprocFormService = beanLocator.get(BprocFormService.class);
        if (processInstanceData != null) {
            UserSession userSession = beanLocator.get(UserSession.class);
            BprocTaskService bprocTaskService = beanLocator.get(BprocTaskService.class);
            List<TaskData> tasks = bprocTaskService.createTaskDataQuery()
                    .processDefinitionKey(getProcDefinitionKey())
                    .taskAssignee(userSession.getCurrentOrSubstitutedUser())
                    .processVariableValueEquals("entityId", getEditedEntity().getId())
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
                ((HBoxLayout) screen.getWindow().getComponentNN("procActionButtonHBox")).add(outcomesPanel);
            }
        } else {
            Button button = uiComponents.create(Button.class);
            button.setId("startProcess");
            button.setCaption("Start process");
            button.setAction(new BaseAction("startProcess").withHandler(actionPerformedEvent -> {
                ProcessDefinitionData latestProcessDefinition = this.findLatestProcessDefinition(getProcDefinitionKey(), beanLocator.get(BprocRepositoryService.class));
//                if (latestProcessDefinition == null) {
////            this.notifications.create(Notifications.NotificationType.ERROR).withCaption(this.messageBundle.getMessage("processDefinitionNotFound")).show(); todo
//                } else {
//                    beanLocator.get(ProcessFormScreens.class).createStartProcessForm(latestProcessDefinition, (FrameOwner) this).show();
//                }

                ProcessFormScreenCreatorsBean processFormScreenCreatorsBean = beanLocator.get(ProcessFormScreenCreatorsBean.class);

                FormData formData = bprocFormService.getStartFormData(latestProcessDefinition.getId());
                String formType = formData != null ? formData.getType() : null;
                formData.setBusinessKey(getEditedEntity().getId().toString());
                ProcessFormScreenCreator formCreator = formType != null ? processFormScreenCreatorsBean.getScreenCreator(formType) : processFormScreenCreatorsBean.getDefaultScreenCreator();
                if (formCreator != null) {
                    ProcessFormScreenCreator.CreationContext creationContext = new ProcessFormScreenCreator.CreationContext(latestProcessDefinition, formData, (FrameOwner) this);
                    formCreator.createStartProcessScreen(creationContext);
                } else {
                    throw new RuntimeException("Unsupported form type " + formType);
                }
            }));
            ((HBoxLayout) screen.getWindow().getComponentNN("procActionButtonHBox")).add(button);

        }
    }

    @Nullable
    default ProcessDefinitionData findLatestProcessDefinition(String processDefinitionKey,
                                                              BprocRepositoryService bprocRepositoryService) {
        List<ProcessDefinitionData> list = bprocRepositoryService.createProcessDefinitionDataQuery().processDefinitionKey(processDefinitionKey).latestVersion().active().list();
        return !list.isEmpty() ? (ProcessDefinitionData) list.get(0) : null;
    }

    String getProcDefinitionKey();
}
