package kz.uco.tsadv.web.screens.dicshift;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.DicShift;

@UiController("tsadv_DicShift.edit")
@UiDescriptor("dic-shift-edit.xml")
@EditedEntityContainer("dicShiftDc")
@LoadDataBeforeShow
public class DicShiftEdit extends StandardEditor<DicShift> {
}