package kz.uco.tsadv.web.screens.guardian;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.Guardian;

@UiController("tsadv_Guardian.edit")
@UiDescriptor("guardian-edit.xml")
@EditedEntityContainer("guardianDc")
@LoadDataBeforeShow
public class GuardianEdit extends StandardEditor<Guardian> {
}