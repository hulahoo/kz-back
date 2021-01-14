package kz.uco.tsadv.web.screens.insurancecontract;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.InsuranceContract;

@UiController("tsadv$InsuranceContract.browse")
@UiDescriptor("insurance-contract-browse.xml")
@LookupComponent("insuranceContractsTable")
@LoadDataBeforeShow
public class InsuranceContractBrowse extends StandardLookup<InsuranceContract> {
}