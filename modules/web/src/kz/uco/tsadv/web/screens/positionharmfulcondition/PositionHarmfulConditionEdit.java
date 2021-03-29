package kz.uco.tsadv.web.screens.positionharmfulcondition;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.entity.tb.PositionHarmfulCondition;

@UiController("tsadv_PositionHarmfulCondition.edit")
@UiDescriptor("position-harmful-condition-edit.xml")
@EditedEntityContainer("positionHarmfulConditionDc")
@LoadDataBeforeShow
public class PositionHarmfulConditionEdit extends StandardEditor<PositionHarmfulCondition> {
}