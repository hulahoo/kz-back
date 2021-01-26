package kz.uco.tsadv.web.screens.insuredperson;

import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.gui.*;
import com.haulmont.cuba.gui.actions.list.BulkEditAction;
import com.haulmont.cuba.gui.app.core.bulk.ColumnsMode;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.gui.screen.LookupComponent;
import io.swagger.models.auth.In;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.personal.dictionary.DicMICAttachmentStatus;
import kz.uco.tsadv.modules.personal.model.InsuranceContract;
import kz.uco.tsadv.modules.personal.model.InsuredPerson;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

@UiController("tsadv$InsuredPerson.browse")
@UiDescriptor("insured-person-browse.xml")
@LookupComponent("insuredPersonsTable")
@LoadDataBeforeShow
public class InsuredPersonBrowse extends StandardLookup<InsuredPerson> {
    @Inject
    private CollectionLoader<InsuredPerson> insuredPersonsDl;
    @Inject
    private ScreenBuilders screenBuilders;
    @Inject
    private Metadata metadata;
    @Inject
    private DataGrid<InsuredPerson> insuredPersonsTable;
    @Inject
    private CommonService commonService;
    protected InsuranceContract insuranceContract;
    @Inject
    private TimeSource timeSource;
    @Named("insuredPersonsTable.bulkEdit")
    private BulkEditAction insuredPersonBulkEdit;
    @Inject
    private BulkEditors bulkEditors;
    @Inject
    private UiComponents uiComponents;
    @Inject
    private Notifications notifications;


    @Subscribe
    public void onInit(InitEvent event) {
        DataGrid.Column column = insuredPersonsTable.addGeneratedColumn("code", new DataGrid.ColumnGenerator<InsuredPerson, LinkButton>(){
            @Override
            public LinkButton getValue(DataGrid.ColumnGeneratorEvent<InsuredPerson> event){
                LinkButton linkButton = uiComponents.create(LinkButton.class);
                linkButton.setCaption(event.getItem().getIin());
                linkButton.setAction(new BaseAction("code").withHandler(e->{
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

        }, 1);
        column.setRenderer(insuredPersonsTable.createRenderer(DataGrid.ComponentRenderer.class));
    }


    public void setParameter(InsuranceContract contract) {
        insuranceContract = contract;
        insuredPersonsDl.setParameter("insuranceContractId", contract.getId());
        insuredPersonsDl.load();
    }

    @Subscribe("insuredPersonsTable.create")
    public void onInsuredPersonsTableCreate(Action.ActionPerformedEvent event) {
        InsuredPerson insuredPerson = metadata.create(InsuredPerson.class);
        insuredPerson.setInsuranceContract(insuranceContract);
//        insuredPerson.setAddressType(insuranceContract.getDefaultAddress());
        insuredPerson.setDocumentType(insuranceContract.getDefaultDocumentType());
        insuredPerson.setAttachDate(timeSource.currentTimestamp());
        insuredPerson.setCompany(insuranceContract.getCompany());
        insuredPerson.setInsuranceProgram(insuranceContract.getInsuranceProgram());
//        insuredPerson.setAddressType(insuranceContract.getDefaultAddress());
        insuredPerson.setStatusRequest(commonService.getEntity(DicMICAttachmentStatus.class, "DRAFT"));

        InsuredPersonEdit editorBuilder = (InsuredPersonEdit) screenBuilders.editor(insuredPersonsTable)
                .newEntity(insuredPerson)
                .build();

        editorBuilder.setParameter("joinHr");
        editorBuilder.show();

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

    @Subscribe("insuredPersonsTable.bulkEdit")
    public void onInsuredPersonsTableBulkEdit(Action.ActionPerformedEvent event) {
        bulkEditors.builder(metadata.getClassNN(InsuredPerson.class), insuredPersonsTable.getSelected(), this)
                .withListComponent(insuredPersonsTable)
                .withColumnsMode(ColumnsMode.TWO_COLUMNS)
                .withIncludeProperties(Arrays.asList("attachDate", "statusRequest", "exclusionDate", "comment"))
                .create()

                .show();
    }


    public Component generateCode(InsuredPerson entity) {
        LinkButton linkButton = uiComponents.create(LinkButton.class);
        linkButton.setCaption("entity.getInsuranceContract().getContract()");
        linkButton.setAction(new BaseAction("code").withHandler(e->{
            screenBuilders.editor(insuredPersonsTable)
                    .editEntity(entity)
                    .build()
                    .show();
        }));
        return linkButton;
    }


    public void bulkEdt() {
        Set<InsuredPerson> bulks = insuredPersonsTable.getSelected();

        InsuredPersonBulkEdit bulkEdit = screenBuilders.editor(InsuredPerson.class, this)
                .withScreenClass(InsuredPersonBulkEdit.class)
                .withAfterCloseListener(e->{
                    int bulkItemSize = bulks.size();
                    notifications.create().withCaption("Изменено " + bulkItemSize + " запись");
                    insuredPersonsDl.load();
                })
                .build();
        if (insuredPersonsTable.getSelected() != null && bulks.size() != 0){
            bulkEdit.setParameter(bulks);
            bulkEdit.show();
        }
    }
}