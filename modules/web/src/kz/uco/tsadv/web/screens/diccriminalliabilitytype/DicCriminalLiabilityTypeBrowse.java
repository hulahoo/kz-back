package kz.uco.tsadv.web.screens.diccriminalliabilitytype;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.dictionary.DicCriminalLiabilityType;

@UiController("tsadv_DicCriminalLiabilityType.browse")
@UiDescriptor("dic-criminal-liability-type-browse.xml")
@LookupComponent("dicCriminalLiabilityTypesTable")
@LoadDataBeforeShow
public class DicCriminalLiabilityTypeBrowse extends StandardLookup<DicCriminalLiabilityType> {
}