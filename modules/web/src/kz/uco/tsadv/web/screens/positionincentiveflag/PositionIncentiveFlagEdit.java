package kz.uco.tsadv.web.screens.positionincentiveflag;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.PositionIncentiveFlag;

@UiController("tsadv_PositionIncentiveFlag.edit")
@UiDescriptor("position-incentive-flag-edit.xml")
@EditedEntityContainer("positionIncentiveFlagDc")
@LoadDataBeforeShow
public class PositionIncentiveFlagEdit extends StandardEditor<PositionIncentiveFlag> {
}