package kz.uco.tsadv.web.screens.childdescription;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.ChildDescription;

@UiController("tsadv_ChildDescription.edit")
@UiDescriptor("child-description-edit.xml")
@EditedEntityContainer("childDescriptionDc")
@LoadDataBeforeShow
public class ChildDescriptionEdit extends StandardEditor<ChildDescription> {
}