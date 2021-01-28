package kz.uco.tsadv.web.screens.insurancecontractadministrator;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.InsuranceContractAdministrator;

@UiController("tsadv$InsuranceContractAdministrator.browse")
@UiDescriptor("insurance-contract-administrator-browse.xml")
@LookupComponent("insuranceContractAdministratorsTable")
@LoadDataBeforeShow
public class InsuranceContractAdministratorBrowse extends StandardLookup<InsuranceContractAdministrator> {
}