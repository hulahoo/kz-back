package kz.uco.tsadv.web.screens.personadwardrequest;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.PersonAdwardRequest;

@UiController("tsadv_PersonAdwardRequest.edit")
@UiDescriptor("person-adward-request-edit.xml")
@EditedEntityContainer("personAdwardRequestDc")
@LoadDataBeforeShow
public class PersonAdwardRequestEdit extends StandardEditor<PersonAdwardRequest> {
}