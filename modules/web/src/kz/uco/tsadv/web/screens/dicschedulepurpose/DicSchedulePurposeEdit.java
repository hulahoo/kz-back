package kz.uco.tsadv.web.screens.dicschedulepurpose;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.dictionary.DicSchedulePurpose;

@UiController("tsadv_DicSchedulePurpose.edit")
@UiDescriptor("dic-schedule-purpose-edit.xml")
@EditedEntityContainer("dicSchedulePurposeDc")
@LoadDataBeforeShow
public class DicSchedulePurposeEdit extends StandardEditor<DicSchedulePurpose> {
}