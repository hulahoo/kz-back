package kz.uco.tsadv.web.screens.insurancecontract;

import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.actions.list.RemoveAction;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.DataGrid;
import com.haulmont.cuba.gui.components.GroupTable;
import com.haulmont.cuba.gui.components.LinkButton;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.InsuranceContract;
import kz.uco.tsadv.modules.personal.model.InsuredPerson;
import kz.uco.tsadv.web.screens.insuredperson.InsuredPersonBrowse;
import kz.uco.tsadv.web.screens.insuredperson.InsuredPersonEdit;
import kz.uco.tsadv.web.screens.insuredperson.InsuredPersonMemberEdit;

import javax.inject.Inject;
import javax.inject.Named;

@UiController("tsadv$InsuranceContract.browse")
@UiDescriptor("insurance-contract-browse.xml")
@LookupComponent("insuranceContractsTable")
@LoadDataBeforeShow
public class InsuranceContractBrowse extends StandardLookup<InsuranceContract> {
    @Inject
    private ScreenBuilders screenBuilders;
    @Inject
    private DataGrid<InsuranceContract> insuranceContractsTable;
    @Inject
    private Button removeBtn;
    @Inject
    private UiComponents uiComponents;


    @Subscribe
    public void onInit(InitEvent event) {
        DataGrid.Column column = insuranceContractsTable.addGeneratedColumn("contractField", new DataGrid.ColumnGenerator<InsuranceContract, LinkButton>(){
            @Override
            public LinkButton getValue(DataGrid.ColumnGeneratorEvent<InsuranceContract> event){
                LinkButton linkButton = uiComponents.create(LinkButton.class);
                linkButton.setCaption(event.getItem().getContract());
                linkButton.setAction(new BaseAction("contractField").withHandler(e->{
                    screenBuilders.editor(insuranceContractsTable)
                            .withScreenClass(InsuranceContractEdit.class)
                            .editEntity(event.getItem())
                            .build().show();
                }));
                return linkButton;
            }

            @Override
            public Class<LinkButton> getType(){
                return LinkButton.class;
            }

        }, 0);
        column.setRenderer(insuranceContractsTable.createRenderer(DataGrid.ComponentRenderer.class));
    }


    @Subscribe(id = "insuranceContractsDc", target = Target.DATA_CONTAINER)
    protected void onInsuranceContractsDcItemChange(InstanceContainer.ItemChangeEvent<InsuranceContract> event) {

        boolean isEnabled = event.getItem() != null && event.getItem().getProgramConditions().size() == 0
                && event.getItem().getAttachments().size() == 0 && event.getItem().getContractAdministrator().size() == 0;
        removeBtn.setEnabled(isEnabled);
    }

//
//    public void showInsuredPersons() {
//        InsuranceContract contract = insuranceContractsTable.getSingleSelected();
//
//        InsuredPersonBrowse insuredPersonBrowse = screenBuilders.screen(this)
//                .withScreenClass(InsuredPersonBrowse.class)
//                .build();
//
//        if (contract != null && contract.getCompany() != null){
//            insuredPersonBrowse.setParameter(contract);
//            insuredPersonBrowse.show();
//        }
//    }
}