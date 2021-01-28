package kz.uco.tsadv.web.screens.insurancecontract;


import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.Screens;
import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.model.*;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.entity.tb.Attachment;
import kz.uco.tsadv.modules.personal.dictionary.DicMICAttachmentStatus;
import kz.uco.tsadv.modules.personal.model.ContractConditions;
import kz.uco.tsadv.modules.personal.model.InsuranceContract;
import kz.uco.tsadv.modules.personal.model.InsuranceContractAdministrator;
import kz.uco.tsadv.modules.personal.model.InsuredPerson;
import kz.uco.tsadv.web.screens.insuredperson.InsuredPersonBrowse;
import kz.uco.tsadv.web.screens.insuredperson.InsuredPersonBulkEdit;
import kz.uco.tsadv.web.screens.insuredperson.InsuredPersonEdit;

import javax.inject.Inject;
import java.time.Instant;
import java.util.Date;
import java.util.Set;

@UiController("tsadv$InsuranceContract.edit")
@UiDescriptor("insurance-contract-edit.xml")
@EditedEntityContainer("insuranceContractDc")
@LoadDataBeforeShow
public class InsuranceContractEdit extends StandardEditor<InsuranceContract> {
    @Inject
    private CollectionPropertyContainer<Attachment> attachmentsDc;
    @Inject
    private DataGrid<ContractConditions> programConditionsDataGrid;
    @Inject
    private HBoxLayout insuredPersonsHbox;
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
    @Inject
    private Screens screens;
    @Inject
    private CollectionLoader<InsuredPerson> insuredPersonsDl;
    @Inject
    private InstanceContainer<InsuranceContract> insuranceContractDc;
    @Inject
    private DataGrid<InsuredPerson> insuredPersonsTable;
    @Inject
    private UiComponents uiComponents;
    @Inject
    private ScreenBuilders screenBuilders;
    @Inject
    private TimeSource timeSource;
    @Inject
    private CommonService commonService;
    @Inject
    private Button createBtnPerson;
    protected boolean isCreateContract;

    @Subscribe
    public void onInit(InitEvent event) {

        DataGrid.Column column = insuredPersonsTable.addGeneratedColumn("contractFieldPerson", new DataGrid.ColumnGenerator<InsuredPerson, LinkButton>(){
            @Override
            public LinkButton getValue(DataGrid.ColumnGeneratorEvent<InsuredPerson> event){
                LinkButton linkButton = uiComponents.create(LinkButton.class);
                linkButton.setCaption(event.getItem().getInsuranceContract().getContract());
                linkButton.setAction(new BaseAction("contractFieldPerson").withHandler(e->{
                    InsuredPersonEdit editorBuilder = (InsuredPersonEdit) screenBuilders.editor(insuredPersonsTable)
                            .editEntity(event.getItem())
                            .build();
                    editorBuilder.setParameter("editHr");
                    editorBuilder.show();
                }));
                return linkButton;
            }

            @Override
            public Class<LinkButton> getType(){
                return LinkButton.class;
            }

        }, 0);
        column.setRenderer(insuredPersonsTable.createRenderer(DataGrid.ComponentRenderer.class));
    }


    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        if (isCreateContract){
            createBtnPerson.setEnabled(false);
        }
    }
//
    @Subscribe
    public void onAfterShow(AfterShowEvent event) {
        insuredPersonsDl.setParameter("insuranceContractId", insuranceContractDc.getItem().getId());
        insuredPersonsDl.load();
    }

    public void setParameter(boolean isCreateContract){
        this.isCreateContract = isCreateContract;
    }


    @Subscribe("insuredPersonsTable.edit")
    public void onInsuredPersonsTableEdit(Action.ActionPerformedEvent event) {
        InsuredPerson selectItem = insuredPersonsTable.getSingleSelected();
        if (selectItem != null){
            InsuredPersonEdit editorBuilder = (InsuredPersonEdit) screenBuilders.editor(insuredPersonsTable)
                    .newEntity(selectItem)
                    .build();
            editorBuilder.setParameter("editHr");
            editorBuilder.show();
        }
    }


    @Subscribe("insuredPersonsTable.bulk")
    public void onInsuredPersonsTableBulk(Action.ActionPerformedEvent event) {
        insuredPersonsTable.getSelected();
        Set<InsuredPerson> bulks = insuredPersonsTable.getSelected();

        InsuredPersonBulkEdit bulkEdit = screenBuilders.editor(InsuredPerson.class, this)
                .withScreenClass(InsuredPersonBulkEdit.class)
                .withAfterCloseListener(e->{
                    int bulkItemSize = bulks.size();
                    notifications.create().withCaption("Изменено " + bulkItemSize + " запись");
                    insuredPersonsDl.load();
                })
                .build();
        if (bulks.size() != 0){
            bulkEdit.setParameter(bulks);
            bulkEdit.show();
        }

    }


    @Subscribe
    public void onBeforeCommitChanges(BeforeCommitChangesEvent event) {
        DataContext dataContext = event.getDataContext();
        contractAdministratorDc.getItems().forEach(dataContext::merge);
        conditionDc.getItems().forEach(dataContext::merge);
        attachmentsDc.getItems().forEach(dataContext::merge);
    }


    @Subscribe("insuredPersonsTable.create")
    public void onInsuredPersonsTableCreate(Action.ActionPerformedEvent event) {
        InsuredPerson insuredPerson = metadata.create(InsuredPerson.class);
        insuredPerson.setInsuranceContract(insuranceContractDc.getItem());
//        insuredPerson.setAddressType(insuranceContract.getDefaultAddress());
        insuredPerson.setDocumentType(insuranceContractDc.getItem().getDefaultDocumentType());
        insuredPerson.setAttachDate(timeSource.currentTimestamp());
        insuredPerson.setCompany(insuranceContractDc.getItem().getCompany());
        insuredPerson.setInsuranceProgram(insuranceContractDc.getItem().getInsuranceProgram());
//        insuredPerson.setAddressType(insuranceContract.getDefaultAddress());
        insuredPerson.setStatusRequest(commonService.getEntity(DicMICAttachmentStatus.class, "DRAFT"));

        InsuredPersonEdit editorBuilder = (InsuredPersonEdit) screenBuilders.editor(insuredPersonsTable)
                .newEntity(insuredPerson)
                .build();

        editorBuilder.setParameter("joinHr");
        editorBuilder.show();

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