package kz.uco.tsadv.web.screens.dicshift;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.DicShift;

@UiController("tsadv_DicShift.browse")
@UiDescriptor("dic-shift-browse.xml")
@LookupComponent("dicShiftsTable")
@LoadDataBeforeShow
public class DicShiftBrowse extends StandardLookup<DicShift> {
}