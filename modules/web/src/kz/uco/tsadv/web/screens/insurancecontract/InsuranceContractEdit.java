package kz.uco.tsadv.web.screens.insurancecontract;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.InsuranceContract;

@UiController("tsadv$InsuranceContract.edit")
@UiDescriptor("insurance-contract-edit.xml")
@EditedEntityContainer("insuranceContractDc")
@LoadDataBeforeShow
public class InsuranceContractEdit extends StandardEditor<InsuranceContract> {
}