package kz.uco.tsadv.web.screens.dicpurposeabsence;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.dictionary.DicPurposeAbsence;

@UiController("tsadv_DicPurposeAbsence.browse")
@UiDescriptor("dic-purpose-absence-browse.xml")
@LookupComponent("dicPurposeAbsencesTable")
@LoadDataBeforeShow
public class DicPurposeAbsenceBrowse extends StandardLookup<DicPurposeAbsence> {
}