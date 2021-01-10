package kz.uco.tsadv.web.screens.personqualificationrequest;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.entity.tb.PersonQualificationRequest;

@UiController("tsadv_PersonQualificationRequest.edit")
@UiDescriptor("person-qualification-request-edit.xml")
@EditedEntityContainer("personQualificationRequestDc")
@LoadDataBeforeShow
public class PersonQualificationRequestEdit extends StandardEditor<PersonQualificationRequest> {
}