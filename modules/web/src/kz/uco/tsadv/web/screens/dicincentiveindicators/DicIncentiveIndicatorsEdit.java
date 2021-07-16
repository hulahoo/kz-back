package kz.uco.tsadv.web.screens.dicincentiveindicators;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.dictionary.DicIncentiveIndicators;

@UiController("tsadv_DicIncentiveIndicators.edit")
@UiDescriptor("dic-incentive-indicators-edit.xml")
@EditedEntityContainer("dicIncentiveIndicatorsDc")
@LoadDataBeforeShow
public class DicIncentiveIndicatorsEdit extends StandardEditor<DicIncentiveIndicators> {
}