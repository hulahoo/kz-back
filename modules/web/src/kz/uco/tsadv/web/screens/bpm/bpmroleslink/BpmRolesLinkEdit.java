package kz.uco.tsadv.web.screens.bpm.bpmroleslink;

import com.haulmont.addon.bproc.entity.ProcessDefinitionData;
import com.haulmont.addon.bproc.service.BprocRepositoryService;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.bpm.BpmRolesLink;
import kz.uco.tsadv.service.BprocService;

import javax.inject.Inject;
import java.util.Map;

@UiController("tsadv$BpmRolesLink.edit")
@UiDescriptor("bpm-roles-link-edit.xml")
@EditedEntityContainer("bpmRolesLinkDc")
@LoadDataBeforeShow
public class BpmRolesLinkEdit extends StandardEditor<BpmRolesLink> {

    @Inject
    protected BprocService bprocService;
    @Inject
    protected BprocRepositoryService bprocRepositoryService;

    @Inject
    protected LookupField<String> bprocUserTaskCodeField;

    @Subscribe
    protected void onBeforeShow(BeforeShowEvent event) {
        ProcessDefinitionData processDefinitionData = bprocRepositoryService
                .createProcessDefinitionDataQuery()
                .processDefinitionKey(getEditedEntity().getBpmRolesDefiner().getProcessDefinitionKey())
                .active()
                .latestVersion()
                .singleResult();
        Map<String, String> activityIdMap = bprocService.getActivityIdMap(processDefinitionData.getId());
        bprocUserTaskCodeField.setOptionsMap(activityIdMap);
    }
}