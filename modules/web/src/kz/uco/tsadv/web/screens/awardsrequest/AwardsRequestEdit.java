package kz.uco.tsadv.web.screens.awardsrequest;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.AwardsRequest;

@UiController("tsadv_AwardsRequest.edit")
@UiDescriptor("awards-request-edit.xml")
@EditedEntityContainer("awardsRequestDc")
@LoadDataBeforeShow
public class AwardsRequestEdit extends StandardEditor<AwardsRequest> {
}