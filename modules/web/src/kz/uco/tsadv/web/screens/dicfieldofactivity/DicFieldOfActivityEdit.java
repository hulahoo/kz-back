package kz.uco.tsadv.web.screens.dicfieldofactivity;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.dictionary.DicFieldOfActivity;

@UiController("tsadv_DicFieldOfActivity.edit")
@UiDescriptor("dic-field-of-activity-edit.xml")
@EditedEntityContainer("dicFieldOfActivityDc")
@LoadDataBeforeShow
public class DicFieldOfActivityEdit extends StandardEditor<DicFieldOfActivity> {
}