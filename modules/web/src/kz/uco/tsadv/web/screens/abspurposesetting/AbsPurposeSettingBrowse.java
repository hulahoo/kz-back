package kz.uco.tsadv.web.screens.abspurposesetting;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.AbsPurposeSetting;

@UiController("tsadv_AbsPurposeSetting.browse")
@UiDescriptor("abs-purpose-setting-browse.xml")
@LookupComponent("absPurposeSettingsTable")
@LoadDataBeforeShow
public class AbsPurposeSettingBrowse extends StandardLookup<AbsPurposeSetting> {
}