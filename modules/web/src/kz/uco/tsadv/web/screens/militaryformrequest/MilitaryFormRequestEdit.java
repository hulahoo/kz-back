package kz.uco.tsadv.web.screens.militaryformrequest;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.MilitaryFormRequest;

@UiController("tsadv_MilitaryFormRequest.edit")
@UiDescriptor("military-form-request-edit.xml")
@EditedEntityContainer("militaryFormRequestDc")
@LoadDataBeforeShow
public class MilitaryFormRequestEdit extends StandardEditor<MilitaryFormRequest> {
}