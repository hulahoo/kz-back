package kz.uco.tsadv.web.screens.personconviction;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.PersonConviction;

@UiController("tsadv_PersonConviction.edit")
@UiDescriptor("person-conviction-edit.xml")
@EditedEntityContainer("personConvictionDc")
@LoadDataBeforeShow
public class PersonConvictionEdit extends StandardEditor<PersonConviction> {
}