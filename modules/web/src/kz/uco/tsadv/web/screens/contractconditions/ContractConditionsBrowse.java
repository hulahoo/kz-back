package kz.uco.tsadv.web.screens.contractconditions;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.ContractConditions;

@UiController("tsadv$ContractConditions.browse")
@UiDescriptor("contract-conditions-browse.xml")
@LookupComponent("contractConditionsesTable")
@LoadDataBeforeShow
public class ContractConditionsBrowse extends StandardLookup<ContractConditions> {
}