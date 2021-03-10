package kz.uco.tsadv.web.screens.dicpurposeabsence;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.dictionary.DicPurposeAbsence;

@UiController("tsadv_DicPurposeAbsence.edit")
@UiDescriptor("dic-purpose-absence-edit.xml")
@EditedEntityContainer("dicPurposeAbsenceDc")
@LoadDataBeforeShow
public class DicPurposeAbsenceEdit extends StandardEditor<DicPurposeAbsence> {
}