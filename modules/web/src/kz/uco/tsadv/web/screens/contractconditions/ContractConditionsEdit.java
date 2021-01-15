package kz.uco.tsadv.web.screens.contractconditions;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.ContractConditions;

@UiController("tsadv$ContractConditions.edit")
@UiDescriptor("contract-conditions-edit.xml")
@EditedEntityContainer("contractConditionsDc")
@LoadDataBeforeShow
public class ContractConditionsEdit extends StandardEditor<ContractConditions> {
}