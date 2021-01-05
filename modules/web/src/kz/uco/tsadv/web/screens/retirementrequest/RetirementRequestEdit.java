package kz.uco.tsadv.web.screens.retirementrequest;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.RetirementRequest;

@UiController("tsadv_RetirementRequest.edit")
@UiDescriptor("retirement-request-edit.xml")
@EditedEntityContainer("retirementRequestDc")
@LoadDataBeforeShow
public class RetirementRequestEdit extends StandardEditor<RetirementRequest> {
}