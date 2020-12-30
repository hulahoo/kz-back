package kz.uco.tsadv.web.screens.personbenefitrequest;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.PersonBenefitRequest;

@UiController("tsadv_PersonBenefitRequest.edit")
@UiDescriptor("person-benefit-request-edit.xml")
@EditedEntityContainer("personBenefitRequestDc")
@LoadDataBeforeShow
public class PersonBenefitRequestEdit extends StandardEditor<PersonBenefitRequest> {
}