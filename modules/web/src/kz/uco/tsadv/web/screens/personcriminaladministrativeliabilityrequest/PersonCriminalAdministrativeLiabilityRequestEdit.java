package kz.uco.tsadv.web.screens.personcriminaladministrativeliabilityrequest;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.PersonCriminalAdministrativeLiabilityRequest;

@UiController("tsadv_PersonCriminalAdministrativeLiabilityRequest.edit")
@UiDescriptor("person-criminal-administrative-liability-request-edit.xml")
@EditedEntityContainer("personCriminalAdministrativeLiabilityRequestDc")
@LoadDataBeforeShow
public class PersonCriminalAdministrativeLiabilityRequestEdit extends StandardEditor<PersonCriminalAdministrativeLiabilityRequest> {
}