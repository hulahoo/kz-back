package kz.uco.tsadv.web.screens.disabilityrequest;

import com.haulmont.cuba.gui.components.HasValue;
import com.haulmont.cuba.gui.components.TextField;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.enums.YesNoEnum;
import kz.uco.tsadv.modules.personal.model.DisabilityRequest;

import javax.inject.Inject;

@UiController("tsadv_DisabilityRequest.edit")
@UiDescriptor("disability-request-edit.xml")
@EditedEntityContainer("disabilityRequestDc")
@LoadDataBeforeShow
public class DisabilityRequestEdit extends StandardEditor<DisabilityRequest> {

    @Inject
    protected TextField<String> groupField;

    @Subscribe("haveDisabilityField")
    protected void onHaveDisabilityFieldValueChange(HasValue.ValueChangeEvent<YesNoEnum> event) {
        groupField.setRequired(event.getValue() != null && event.getValue().equals(YesNoEnum.YES));
    }

}