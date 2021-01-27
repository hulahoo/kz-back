package kz.uco.tsadv.web.screens.abspurposesetting;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.AbsPurposeSetting;

@UiController("tsadv_AbsPurposeSetting.edit")
@UiDescriptor("abs-purpose-setting-edit.xml")
@EditedEntityContainer("absPurposeSettingDc")
@LoadDataBeforeShow
public class AbsPurposeSettingEdit extends StandardEditor<AbsPurposeSetting> {
}