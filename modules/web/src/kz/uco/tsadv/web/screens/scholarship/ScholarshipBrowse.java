package kz.uco.tsadv.web.screens.scholarship;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.Scholarship;

@UiController("tsadv_Scholarship.browse")
@UiDescriptor("scholarship-browse.xml")
@LookupComponent("scholarshipsTable")
@LoadDataBeforeShow
public class ScholarshipBrowse extends StandardLookup<Scholarship> {
}