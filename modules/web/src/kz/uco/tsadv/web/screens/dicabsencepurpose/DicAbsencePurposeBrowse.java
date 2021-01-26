package kz.uco.tsadv.web.screens.dicabsencepurpose;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.dictionary.DicAbsencePurpose;

@UiController("tsadv_DicAbsencePurpose.browse")
@UiDescriptor("dic-absence-purpose-browse.xml")
@LookupComponent("dicAbsencePurposesTable")
@LoadDataBeforeShow
public class DicAbsencePurposeBrowse extends StandardLookup<DicAbsencePurpose> {
}