package kz.uco.tsadv.web.screens.dicabsencepurpose;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.dictionary.DicAbsencePurpose;

@UiController("tsadv_DicAbsencePurpose.edit")
@UiDescriptor("dic-absence-purpose-edit.xml")
@EditedEntityContainer("dicAbsencePurposeDc")
@LoadDataBeforeShow
public class DicAbsencePurposeEdit extends StandardEditor<DicAbsencePurpose> {
}