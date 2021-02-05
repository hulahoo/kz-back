package kz.uco.tsadv.web.screens.contractconditions;

import com.haulmont.cuba.gui.components.HasValue;
import com.haulmont.cuba.gui.components.TextField;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.ContractConditions;

import javax.inject.Inject;
import java.math.BigDecimal;

@UiController("tsadv$ContractConditions.edit")
@UiDescriptor("contract-conditions-edit.xml")
@EditedEntityContainer("contractConditionsDc")
@LoadDataBeforeShow
public class ContractConditionsEdit extends StandardEditor<ContractConditions> {

    @Inject
    private TextField<BigDecimal> costInKztField;

    @Subscribe("isFreeField")
    public void onIsFreeFieldValueChange(HasValue.ValueChangeEvent<Boolean> event) {
        if (event.getValue().booleanValue()){
            costInKztField.setValue(BigDecimal.valueOf(0));
            costInKztField.setEditable(false);
        }else {
            costInKztField.setValue(null);
            costInKztField.setEditable(true);
        }
    }


}