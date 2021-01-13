package kz.uco.tsadv.web.screens.dicjobgroup;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.performance.dictionary.DicJobGroup;

@UiController("tsadv_DicJobGroup.browse")
@UiDescriptor("dic-job-group-browse.xml")
@LookupComponent("dicJobGroupsTable")
@LoadDataBeforeShow
public class DicJobGroupBrowse extends StandardLookup<DicJobGroup> {
}