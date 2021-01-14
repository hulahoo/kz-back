package kz.uco.tsadv.web.screens.personclinicdispancerrequest;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.PersonClinicDispancerRequest;

@UiController("tsadv_PersonClinicDispancerRequest.edit")
@UiDescriptor("person-clinic-dispancer-request-edit.xml")
@EditedEntityContainer("personClinicDispancerRequestDc")
@LoadDataBeforeShow
public class PersonClinicDispancerRequestEdit extends StandardEditor<PersonClinicDispancerRequest> {
}