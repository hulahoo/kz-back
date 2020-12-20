package kz.uco.tsadv.web.screens.bpm.processforms;

import com.haulmont.addon.bproc.entity.ProcessInstanceData;
import com.haulmont.addon.bproc.service.BprocRuntimeService;
import com.haulmont.addon.bproc.web.processform.ProcessForm;
import com.haulmont.addon.bproc.web.processform.ProcessFormContext;
import com.haulmont.addon.bproc.web.processform.ProcessFormScreenOptions;
import com.haulmont.addon.bproc.web.processform.ProcessVariable;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.TextArea;
import com.haulmont.cuba.gui.screen.Screen;
import com.haulmont.cuba.gui.screen.Subscribe;
import com.haulmont.cuba.gui.screen.UiController;
import com.haulmont.cuba.gui.screen.UiDescriptor;

import javax.inject.Inject;
import java.util.UUID;

@UiController("tsadv_RunProcessDialog")
@UiDescriptor("run-process-dialog.xml.xml")
@ProcessForm
public class RunProcessDialog extends Screen {
    @Inject
    protected BprocRuntimeService bprocRuntimeService;
    @ProcessVariable
    protected String processId;

    protected String processDefinitionKey;
    @Inject
    private TextArea<String> comment;
    UUID entityId;

    @Subscribe
    public void onInit(InitEvent event) {
        ProcessFormContext processFormContext = ((ProcessFormScreenOptions) event.getOptions()).getProcessFormContext();
        processDefinitionKey = processFormContext.getProcessDefinitionData().getKey();
        entityId = UUID.fromString(processFormContext.getFormData().getBusinessKey());
    }

    @Subscribe("startProcessBtn")
    public void onStartProcessBtnClick(Button.ClickEvent event) {
        ProcessInstanceData processInstanceData = bprocRuntimeService.startProcessInstanceByKey(
                processDefinitionKey,
                entityId.toString(),
                ParamsMap.of("startComment", comment.getValue()));
        processId = processInstanceData.getId();
        saveRoute(processInstanceData);
    }

    protected void saveRoute(ProcessInstanceData processInstanceData) {
        processInstanceData.getId();
    }

}