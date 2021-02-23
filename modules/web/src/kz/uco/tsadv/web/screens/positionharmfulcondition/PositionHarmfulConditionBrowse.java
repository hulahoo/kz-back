package kz.uco.tsadv.web.screens.positionharmfulcondition;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.entity.tb.PositionHarmfulCondition;

@UiController("tsadv_PositionHarmfulCondition.browse")
@UiDescriptor("position-harmful-condition-browse.xml")
@LookupComponent("positionHarmfulConditionsTable")
@LoadDataBeforeShow
public class PositionHarmfulConditionBrowse extends StandardLookup<PositionHarmfulCondition> {
}