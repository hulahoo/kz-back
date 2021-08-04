package kz.uco.tsadv.web.screens.dicincentiveindicatorscoresetting;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.DicIncentiveIndicatorScoreSetting;

@UiController("tsadv_DicIncentiveIndicatorScoreSetting.browse")
@UiDescriptor("dic-incentive-indicator-score-setting-browse.xml")
@LookupComponent("dicIncentiveIndicatorScoreSettingsTable")
@LoadDataBeforeShow
public class DicIncentiveIndicatorScoreSettingBrowse extends StandardLookup<DicIncentiveIndicatorScoreSetting> {
}