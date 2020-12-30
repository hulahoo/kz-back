package kz.uco.tsadv.web.screens.dicjobgroup;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.performance.dictionary.DicJobGroup;

@UiController("tsadv_DicJobGroup.edit")
@UiDescriptor("dic-job-group-edit.xml")
@EditedEntityContainer("dicJobGroupDc")
@LoadDataBeforeShow
public class DicJobGroupEdit extends StandardEditor<DicJobGroup> {
}