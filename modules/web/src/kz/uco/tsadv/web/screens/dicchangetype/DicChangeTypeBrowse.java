package kz.uco.tsadv.web.screens.dicchangetype;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.dictionary.DicChangeType;

@UiController("tsadv_DicChangeType.browse")
@UiDescriptor("dic-change-type-browse.xml")
@LookupComponent("dicChangeTypesTable")
@LoadDataBeforeShow
public class DicChangeTypeBrowse extends StandardLookup<DicChangeType> {
}