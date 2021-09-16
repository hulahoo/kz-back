package kz.uco.tsadv.web.screens.dicincentiveindicatortype;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.dictionary.DicIncentiveIndicatorType;

@UiController("tsadv_DicIncentiveIndicatorType.browse")
@UiDescriptor("dic-incentive-indicator-type-browse.xml")
@LookupComponent("dicIncentiveIndicatorTypesTable")
@LoadDataBeforeShow
public class DicIncentiveIndicatorTypeBrowse extends StandardLookup<DicIncentiveIndicatorType> {
}