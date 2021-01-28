package kz.uco.tsadv.web.screens.insurancecontractadministrator;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.InsuranceContractAdministrator;

@UiController("tsadv$InsuranceContractAdministrator.edit")
@UiDescriptor("insurance-contract-administrator-edit.xml")
@EditedEntityContainer("insuranceContractAdministratorDc")
@LoadDataBeforeShow
public class InsuranceContractAdministratorEdit extends StandardEditor<InsuranceContractAdministrator> {
}