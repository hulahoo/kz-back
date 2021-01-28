package kz.uco.tsadv.web.screens.bpm.bpmrolesdefiner;

import com.haulmont.addon.bproc.entity.ProcessDefinitionData;
import com.haulmont.addon.bproc.service.BprocRepositoryService;
import com.haulmont.cuba.gui.actions.list.CreateAction;
import com.haulmont.cuba.gui.components.HasValue;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.components.PickerField;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.CollectionPropertyContainer;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.base.entity.dictionary.DicCompany;
import kz.uco.tsadv.modules.bpm.BpmRolesDefiner;
import kz.uco.tsadv.modules.bpm.BpmRolesLink;

import javax.inject.Inject;
import javax.inject.Named;

@UiController("tsadv$BpmRolesDefiner.edit")
@UiDescriptor("bpm-roles-definer-edit.xml")
@EditedEntityContainer("bpmRolesDefinerDc")
@LoadDataBeforeShow
public class BpmRolesDefinerEdit extends StandardEditor<BpmRolesDefiner> {

    @Inject
    protected CollectionContainer<ProcessDefinitionData> processDefinitionsDc;
    @Inject
    protected CollectionPropertyContainer<BpmRolesLink> linksDc;
    @Inject
    protected BprocRepositoryService bprocRepositoryService;
    @Inject
    protected LookupField<ProcessDefinitionData> processDefinitionKeyField;
    @Named("linksTable.create")
    protected CreateAction<BpmRolesLink> linksTableCreate;
    @Inject
    protected PickerField<DicCompany> companyField;

    @Subscribe
    protected void onBeforeShow(BeforeShowEvent event) {
        initProcessDefinitionItems();
    }

    @Subscribe
    protected void onAfterShow(AfterShowEvent event) {
        linksTableCreate.setEnabled(getEditedEntity().getProcessDefinitionKey() != null);
        companyField.setVisible(getEditedEntity().getCompany() != null);
        String processDefinitionKey = getEditedEntity().getProcessDefinitionKey();
        if (processDefinitionKey != null) {
            ProcessDefinitionData definitionData = processDefinitionsDc.getItems().stream()
                    .filter(processDefinitionData -> processDefinitionKey.equals(processDefinitionData.getKey()))
                    .findAny()
                    .orElse(null);
            processDefinitionKeyField.setValue(definitionData);
        }
    }

    protected void initProcessDefinitionItems() {
        processDefinitionsDc.setItems(bprocRepositoryService.createProcessDefinitionDataQuery()
                .latestVersion()
                .orderByProcessDefinitionKey().asc()
                .orderByProcessDefinitionVersion().asc()
                .list());
    }

    @Subscribe("processDefinitionKeyField")
    protected void onProcessDefinitionKeyFieldValueChange(HasValue.ValueChangeEvent<ProcessDefinitionData> event) {
        ProcessDefinitionData definitionData = event.getValue();
        if (definitionData != null
                && getEditedEntity().getProcessDefinitionKey() != null
                && !getEditedEntity().getProcessDefinitionKey().equals(definitionData.getKey())) {
            linksDc.getMutableItems().clear();
        }
        getEditedEntity().setProcessDefinitionKey(definitionData != null ? definitionData.getKey() : null);
        linksTableCreate.setEnabled(getEditedEntity().getProcessDefinitionKey() != null);
    }
}