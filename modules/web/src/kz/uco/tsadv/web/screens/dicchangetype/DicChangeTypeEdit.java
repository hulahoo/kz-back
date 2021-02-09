package kz.uco.tsadv.web.screens.dicchangetype;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.dictionary.DicChangeType;

@UiController("tsadv_DicChangeType.edit")
@UiDescriptor("dic-change-type-edit.xml")
@EditedEntityContainer("dicChangeTypeDc")
@LoadDataBeforeShow
public class DicChangeTypeEdit extends StandardEditor<DicChangeType> {
}