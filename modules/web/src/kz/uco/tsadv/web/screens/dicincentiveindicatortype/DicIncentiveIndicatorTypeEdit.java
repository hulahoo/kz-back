package kz.uco.tsadv.web.screens.dicincentiveindicatortype;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.dictionary.DicIncentiveIndicatorType;

@UiController("tsadv_DicIncentiveIndicatorType.edit")
@UiDescriptor("dic-incentive-indicator-type-edit.xml")
@EditedEntityContainer("dicIncentiveIndicatorTypeDc")
@LoadDataBeforeShow
public class DicIncentiveIndicatorTypeEdit extends StandardEditor<DicIncentiveIndicatorType> {
}