package kz.uco.tsadv.web.screens.dicincentiveindicators;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.dictionary.DicIncentiveIndicators;

@UiController("tsadv_DicIncentiveIndicators.browse")
@UiDescriptor("dic-incentive-indicators-browse.xml")
@LookupComponent("dicIncentiveIndicatorsesTable")
@LoadDataBeforeShow
public class DicIncentiveIndicatorsBrowse extends StandardLookup<DicIncentiveIndicators> {
}