package kz.uco.tsadv.web.screens.dicreceivingtype;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.dictionary.DicReceivingType;


/**
 * User: maiha
 * Date: 25.12.2020
 * Time: 12:52
 */

@UiController("tsadv_DicReceivingType.browse")
@UiDescriptor("dic-receiving-type-browse.xml")
@LookupComponent("dicReceivingTypesTable")
@LoadDataBeforeShow
public class DicReceivingTypeBrowse extends StandardLookup<DicReceivingType> {
}