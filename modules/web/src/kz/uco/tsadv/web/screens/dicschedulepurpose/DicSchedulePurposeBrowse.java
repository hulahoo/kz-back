package kz.uco.tsadv.web.screens.dicschedulepurpose;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.dictionary.DicSchedulePurpose;

@UiController("tsadv_DicSchedulePurpose.browse")
@UiDescriptor("dic-schedule-purpose-browse.xml")
@LookupComponent("dicSchedulePurposesTable")
@LoadDataBeforeShow
public class DicSchedulePurposeBrowse extends StandardLookup<DicSchedulePurpose> {
}