package kz.uco.tsadv.web.screens.absencerequest;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.mixins.SelfServiceMixin;
import kz.uco.tsadv.modules.personal.model.AbsenceRequest;

@UiController("tsadv$AbsenceRequestNew.edit")
@UiDescriptor("absence-request-new-edit.xml")
@EditedEntityContainer("absenceRequestDc")
@LoadDataBeforeShow
public class AbsenceRequestNewEdit extends StandardEditor<AbsenceRequest> {
}