package kz.uco.tsadv.web.screens.dicreceivingtype;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.dictionary.DicReceivingType;


/**
 * User: maiha
 * Date: 25.12.2020
 * Time: 12:52
 */

@UiController("tsadv_DicReceivingType.edit")
@UiDescriptor("dic-receiving-type-edit.xml")
@EditedEntityContainer("dicReceivingTypeDc")
@LoadDataBeforeShow
public class DicReceivingTypeEdit extends StandardEditor<DicReceivingType> {
}