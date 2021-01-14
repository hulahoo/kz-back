package kz.uco.tsadv.web.screens.scoresetting;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.performance.model.ScoreSetting;

@UiController("tsadv_ScoreSetting.edit")
@UiDescriptor("score-setting-edit.xml")
@EditedEntityContainer("scoreSettingDc")
@LoadDataBeforeShow
public class ScoreSettingEdit extends StandardEditor<ScoreSetting> {
}