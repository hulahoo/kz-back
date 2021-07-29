package kz.uco.tsadv.web.screens.dicfieldofactivity;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.dictionary.DicFieldOfActivity;

@UiController("tsadv_DicFieldOfActivity.browse")
@UiDescriptor("dic-field-of-activity-browse.xml")
@LookupComponent("dicFieldOfActivitiesTable")
@LoadDataBeforeShow
public class DicFieldOfActivityBrowse extends StandardLookup<DicFieldOfActivity> {
}