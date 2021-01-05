package kz.uco.tsadv.web.screens.personexperiencerequest;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.recruitment.model.PersonExperienceRequest;

@UiController("tsadv_PersonExperienceRequest.edit")
@UiDescriptor("person-experience-request-edit.xml")
@EditedEntityContainer("personExperienceRequestDc")
@LoadDataBeforeShow
public class PersonExperienceRequestEdit extends StandardEditor<PersonExperienceRequest> {
}