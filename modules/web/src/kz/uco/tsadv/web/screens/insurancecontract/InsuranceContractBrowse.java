package kz.uco.tsadv.web.screens.insurancecontract;

import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.actions.list.RemoveAction;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.GroupTable;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.InsuranceContract;
import kz.uco.tsadv.web.screens.insuredperson.InsuredPersonBrowse;

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
    private GroupTable<InsuranceContract> insuranceContractsTable;
    @Inject
    private CollectionContainer<InsuranceContract> insuranceContractsDc;
    @Named("insuranceContractsTable.remove")
    private RemoveAction<InsuranceContract> insuranceContractsTableRemove;
    @Inject
    private Button removeBtn;

    @Subscribe(id = "insuranceContractsDc", target = Target.DATA_CONTAINER)
    protected void onInsuranceContractsDcItemChange(InstanceContainer.ItemChangeEvent<InsuranceContract> event) {

        boolean isEnabled = event.getItem() != null && event.getItem().getProgramConditions().size() == 0
                && event.getItem().getAttachments().size() == 0 && event.getItem().getContractAdministrator().size() == 0;
        removeBtn.setEnabled(isEnabled);
    }



    public void showInsuredPersons() {
        InsuranceContract contract = insuranceContractsTable.getSingleSelected();

        InsuredPersonBrowse insuredPersonBrowse = screenBuilders.screen(this)
                .withScreenClass(InsuredPersonBrowse.class)
                .build();

        if (contract != null && contract.getCompany() != null){
            insuredPersonBrowse.setParameter(contract);
            insuredPersonBrowse.show();
        }
    }
}