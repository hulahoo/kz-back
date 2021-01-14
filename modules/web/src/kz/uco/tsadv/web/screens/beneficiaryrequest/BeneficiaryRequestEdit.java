package kz.uco.tsadv.web.screens.beneficiaryrequest;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.components.HasValue;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.BeneficiaryRequest;

import javax.inject.Inject;

@UiController("tsadv_BeneficiaryRequest.edit")
@UiDescriptor("beneficiary-request-edit.xml")
@EditedEntityContainer("beneficiaryRequestDc")
@LoadDataBeforeShow
public class BeneficiaryRequestEdit extends StandardEditor<BeneficiaryRequest> {

    @Inject
    protected InstanceContainer<BeneficiaryRequest> beneficiaryRequestDc;
    @Inject
    protected DataManager dataManager;


    @Subscribe("relatedPersonGroupField")
    protected void onRelatedPersonGroupFieldValueChange(HasValue.ValueChangeEvent<PersonGroupExt> event) {
        if (event.getValue() != null) {
            PersonGroupExt personGroupExt = dataManager.reload(event.getValue(),
                    "personGroupExt-for-beneficary-request-edit");
            beneficiaryRequestDc.getItem().setLastName(personGroupExt.getPerson().getLastName());
            beneficiaryRequestDc.getItem().setFirstName(personGroupExt.getPerson().getFirstName());
            beneficiaryRequestDc.getItem().setMiddleName(personGroupExt.getPerson().getMiddleName());
            beneficiaryRequestDc.getItem().setBirthDate(personGroupExt.getPerson().getDateOfBirth());
        }
    }


}