package kz.uco.tsadv.web.screens.scholarship;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.Scholarship;

@UiController("tsadv_Scholarship.edit")
@UiDescriptor("scholarship-edit.xml")
@EditedEntityContainer("scholarshipDc")
@LoadDataBeforeShow
public class ScholarshipEdit extends StandardEditor<Scholarship> {
}