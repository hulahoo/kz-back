package kz.uco.tsadv.web.screens.personbankdetailsrequest;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.PersonBankdetailsRequest;

@UiController("tsadv_PersonBankdetailsRequest.edit")
@UiDescriptor("person-bankdetails-request-edit.xml")
@EditedEntityContainer("personBankdetailsRequestDc")
@LoadDataBeforeShow
public class PersonBankdetailsRequestEdit extends StandardEditor<PersonBankdetailsRequest> {
}