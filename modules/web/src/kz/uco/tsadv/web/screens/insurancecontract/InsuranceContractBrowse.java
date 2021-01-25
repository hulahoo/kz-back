package kz.uco.tsadv.web.screens.insurancecontract;

import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.components.GroupTable;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.InsuranceContract;
import kz.uco.tsadv.web.screens.insuredperson.InsuredPersonBrowse;

import javax.inject.Inject;

@UiController("tsadv$InsuranceContract.browse")
@UiDescriptor("insurance-contract-browse.xml")
@LookupComponent("insuranceContractsTable")
@LoadDataBeforeShow
public class InsuranceContractBrowse extends StandardLookup<InsuranceContract> {
    @Inject
    private ScreenBuilders screenBuilders;
    @Inject
    private GroupTable<InsuranceContract> insuranceContractsTable;

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