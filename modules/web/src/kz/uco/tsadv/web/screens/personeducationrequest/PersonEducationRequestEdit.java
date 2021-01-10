package kz.uco.tsadv.web.screens.personeducationrequest;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.recruitment.model.PersonEducationRequest;

@UiController("tsadv_PersonEducationRequest.edit")
@UiDescriptor("person-education-request-edit.xml")
@EditedEntityContainer("personEducationRequestDc")
@LoadDataBeforeShow
public class PersonEducationRequestEdit extends StandardEditor<PersonEducationRequest> {
}