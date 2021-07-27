package kz.uco.tsadv.web.screens.dicguardiantype;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.dictionary.DicGuardianType;

@UiController("tsadv_DicGuardianType.browse")
@UiDescriptor("dic-guardian-type-browse.xml")
@LookupComponent("dicGuardianTypesTable")
@LoadDataBeforeShow
public class DicGuardianTypeBrowse extends StandardLookup<DicGuardianType> {
}