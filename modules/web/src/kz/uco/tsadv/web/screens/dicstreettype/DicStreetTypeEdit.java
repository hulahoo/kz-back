package kz.uco.tsadv.web.screens.dicstreettype;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.dictionary.DicStreetType;

@UiController("tsadv_DicStreetType.edit")
@UiDescriptor("dic-street-type-edit.xml")
@EditedEntityContainer("dicStreetTypeDc")
@LoadDataBeforeShow
public class DicStreetTypeEdit extends StandardEditor<DicStreetType> {
}