package kz.uco.tsadv.web.screens.personbenefitrequest;

import com.haulmont.cuba.gui.components.HasValue;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.dictionary.DicReasonBenifit;
import kz.uco.tsadv.modules.personal.enums.YesNoEnum;
import kz.uco.tsadv.modules.personal.model.PersonBenefitRequest;

import javax.inject.Inject;

@UiController("tsadv_PersonBenefitRequest.edit")
@UiDescriptor("person-benefit-request-edit.xml")
@EditedEntityContainer("personBenefitRequestDc")
@LoadDataBeforeShow
public class PersonBenefitRequestEdit extends StandardEditor<PersonBenefitRequest> {
    @Inject
    protected InstanceContainer<PersonBenefitRequest> personBenefitRequestDc;

    @Subscribe("reasonField")
    protected void onReasonFieldValueChange(HasValue.ValueChangeEvent<DicReasonBenifit> event) {
        if (event.getValue() != null && event.getValue().getCode() != null) {
            if (event.getValue().getCode().equals("warrior")) {
                personBenefitRequestDc.getItem().setCombatant(YesNoEnum.YES);
            } else if (event.getValue().getCode().equals("radiation")) {
                personBenefitRequestDc.getItem().setRadiationRiskZone(YesNoEnum.YES);
            }
        }
    }

}