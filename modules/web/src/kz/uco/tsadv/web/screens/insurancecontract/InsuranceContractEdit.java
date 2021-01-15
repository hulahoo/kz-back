package kz.uco.tsadv.web.screens.insurancecontract;

import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.FileStorageException;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.FileUploadField;
import com.haulmont.cuba.gui.components.LinkButton;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.export.ExportDisplay;
import com.haulmont.cuba.gui.export.ExportFormat;
import com.haulmont.cuba.gui.model.CollectionPropertyContainer;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.gui.upload.FileUploadingAPI;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.base.cuba.actions.CreateActionExt;
import kz.uco.tsadv.entity.tb.Attachment;
import kz.uco.tsadv.modules.personal.model.InsuranceContract;
import kz.uco.tsadv.modules.personal.model.InsuranceContractAdministrator;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.util.ArrayList;

@UiController("tsadv$InsuranceContract.edit")
@UiDescriptor("insurance-contract-edit.xml")
@EditedEntityContainer("insuranceContractDc")
@LoadDataBeforeShow
public class InsuranceContractEdit extends StandardEditor<InsuranceContract> {
//
//    @Inject
//    private InstanceContainer<InsuranceContract> insuranceContractDc;
//    @Inject
//    private ComponentsFactory componentsFactory;
//    @Inject
//    private ExportDisplay exportDisplay;
//    @Named("contractAdministratorDataGrid.create")
//    private CreateActionExt contractAdministratorDataGridCreate;
//    @Inject
//    protected FileUploadingAPI fileUploadingAPI;
//    @Inject
//    protected FileUploadField upload;
//    @Inject
//    private CollectionPropertyContainer<Attachment> attachmentsDc;

//
//    public Component generatorName(FileDescriptor fd) {
//        LinkButton linkButton = componentsFactory.createComponent(LinkButton.class);
//        linkButton.setCaption(fd.getName());
//        linkButton.setAction(new BaseAction("export") {
//            @Override
//            public void actionPerform(Component component) {
//                super.actionPerform(component);
//                exportDisplay.show(fd, ExportFormat.OCTET_STREAM);
//            }
//        });
//        return linkButton;
//    }



//    @Inject
//    private DataGrid<ContractConditions> programConditionsDataGrid;
//    @Inject
//    private Notifications notifications;
//    @Inject
//    private Metadata metadata;
//    @Inject
//    private CollectionPropertyContainer<ContractConditions> programConditionsDc;
//
//    @Subscribe("programConditionsDataGrid.create")
//    public void onProgramConditionsDataGridCreate(Action.ActionPerformedEvent event) {
//        if (programConditionsDataGrid.isEditorActive()) {
//            notifications.create()
//                    .withCaption("Close the editor before creating a new entity")
//                    .show();
//            return;
//        }
//        ContractConditions contractCondition = metadata.create(ContractConditions.class);
//        programConditionsDc.getMutableItems().add(contractCondition);
//        programConditionsDataGrid.edit(contractCondition);
//    }
//
//    @Subscribe("programConditionsDataGrid.edit")
//    public void onProgramConditionsDataGridEdit(Action.ActionPerformedEvent event) {
//        ContractConditions selected = programConditionsDataGrid.getSingleSelected();
//        if (selected != null) {
//            programConditionsDataGrid.edit(selected);
//        } else {
//            notifications.create()
//                    .withCaption("Item is not selected")
//                    .show();
//        }
//    }
}