package kz.uco.tsadv.web.screens.dicstreettype;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.dictionary.DicStreetType;

@UiController("tsadv_DicStreetType.browse")
@UiDescriptor("dic-street-type-browse.xml")
@LookupComponent("dicStreetTypesTable")
@LoadDataBeforeShow
public class DicStreetTypeBrowse extends StandardLookup<DicStreetType> {
}