package kz.uco.tsadv.web.screens.insurancecontract;


import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.model.*;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.entity.tb.Attachment;
import kz.uco.tsadv.modules.personal.model.ContractConditions;
import kz.uco.tsadv.modules.personal.model.InsuranceContract;
import kz.uco.tsadv.modules.personal.model.InsuranceContractAdministrator;

import javax.inject.Inject;
import java.time.Instant;
import java.util.Date;

@UiController("tsadv$InsuranceContract.edit")
@UiDescriptor("insurance-contract-edit.xml")
@EditedEntityContainer("insuranceContractDc")
@LoadDataBeforeShow
public class InsuranceContractEdit extends StandardEditor<InsuranceContract> {
    @Inject
    private CollectionPropertyContainer<Attachment> attachmentsDc;
    @Inject
    private DataGrid<ContractConditions> programConditionsDataGrid;
    @Inject DataGrid<InsuranceContractAdministrator> contractAdministratorDataGrid;
    @Inject
    private Notifications notifications;
    @Inject
    private Metadata metadata;
    @Inject
    private CollectionPropertyContainer<InsuranceContractAdministrator> contractAdministratorDc;
    @Inject
    private DateField<Date> expirationDateField;
    @Inject
    private DateField<Date> availabilityPeriodToField;
    @Inject
    private CollectionPropertyContainer<ContractConditions> conditionDc;


    @Subscribe
    public void onBeforeCommitChanges(BeforeCommitChangesEvent event) {
        DataContext dataContext = event.getDataContext();
        contractAdministratorDc.getItems().forEach(dataContext::merge);
        conditionDc.getItems().forEach(dataContext::merge);
        attachmentsDc.getItems().forEach(dataContext::merge);
    }


    @Subscribe("programConditionsDataGrid.create")
    public void onProgramConditionsDataGridCreate(Action.ActionPerformedEvent event) {
        if (programConditionsDataGrid.isEditorActive()) {
            notifications.create()
                    .withCaption("Close the editor before creating a new entity")
                    .show();
            return;
        }
        ContractConditions contractCondition = metadata.create(ContractConditions.class);
        contractCondition.setInsuranceContract(getEditedEntity());
        conditionDc.getMutableItems().add(contractCondition);
        programConditionsDataGrid.edit(contractCondition);
    }


    @Subscribe("programConditionsDataGrid.edit")
    public void onProgramConditionsDataGridEdit(Action.ActionPerformedEvent event) {
        ContractConditions selected = programConditionsDataGrid.getSingleSelected();
        if (selected != null) {
            programConditionsDataGrid.edit(selected);
        } else {
            notifications.create()
                    .withCaption("Item is not selected")
                    .show();
        }
    }


    @Subscribe("contractAdministratorDataGrid.create")
    public void onContractAdministratorDataGridCreate(Action.ActionPerformedEvent event) {
        if (contractAdministratorDataGrid.isEditorActive()) {
            notifications.create()
                    .withCaption("Close the editor before creating a new entity")
                    .show();
            return;
        }
        InsuranceContractAdministrator contractAdministrator = metadata.create(InsuranceContractAdministrator.class);
        contractAdministrator.setNotifyAboutNewAttachments(true);
        contractAdministrator.setInsuranceContract(getEditedEntity());
        contractAdministratorDc.getMutableItems().add(contractAdministrator);
        contractAdministratorDataGrid.edit(contractAdministrator);
    }


    @Subscribe("contractAdministratorDataGrid.edit")
    public void onContractAdministratorDataGridEdit(Action.ActionPerformedEvent event) {
        InsuranceContractAdministrator selected = contractAdministratorDataGrid.getSingleSelected();
        if (selected != null) {
            contractAdministratorDataGrid.edit(selected);
        } else {
            notifications.create()
                    .withCaption("Item is not selected")
                    .show();
        }
    }


    @Subscribe("startDateField")
    public void onStartDateFieldValueChange(HasValue.ValueChangeEvent<Date> event) {

        if (event.getValue() != null) {
            Instant i = Instant.ofEpochMilli(event.getValue().getTime());
            Date outRequestDate = Date.from(i);
            expirationDateField.setRangeStart(outRequestDate);
        }
    }


    @Subscribe("availabilityPeriodFromField")
    public void onAvailabilityPeriodFromFieldValueChange(HasValue.ValueChangeEvent<Date> event) {
        if (event.getValue() != null){
                Instant i = Instant.ofEpochMilli(event.getValue().getTime());
                Date outRequestDate = Date.from(i);
                availabilityPeriodToField.setRangeStart(outRequestDate);
        }
    }

}