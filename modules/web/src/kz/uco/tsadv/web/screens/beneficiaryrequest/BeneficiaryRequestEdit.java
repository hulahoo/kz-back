package kz.uco.tsadv.web.screens.beneficiaryrequest;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.BeneficiaryRequest;

@UiController("tsadv_BeneficiaryRequest.edit")
@UiDescriptor("beneficiary-request-edit.xml")
@EditedEntityContainer("beneficiaryRequestDc")
@LoadDataBeforeShow
public class BeneficiaryRequestEdit extends StandardEditor<BeneficiaryRequest> {
}