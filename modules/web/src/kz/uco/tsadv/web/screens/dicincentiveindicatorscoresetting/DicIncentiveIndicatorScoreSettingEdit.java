package kz.uco.tsadv.web.screens.dicincentiveindicatorscoresetting;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.DicIncentiveIndicatorScoreSetting;

@UiController("tsadv_DicIncentiveIndicatorScoreSetting.edit")
@UiDescriptor("dic-incentive-indicator-score-setting-edit.xml")
@EditedEntityContainer("dicIncentiveIndicatorScoreSettingDc")
@LoadDataBeforeShow
public class DicIncentiveIndicatorScoreSettingEdit extends StandardEditor<DicIncentiveIndicatorScoreSetting> {
}