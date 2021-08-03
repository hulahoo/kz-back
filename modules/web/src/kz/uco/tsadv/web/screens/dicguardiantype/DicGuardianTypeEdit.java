package kz.uco.tsadv.web.screens.dicguardiantype;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.dictionary.DicGuardianType;

@UiController("tsadv_DicGuardianType.edit")
@UiDescriptor("dic-guardian-type-edit.xml")
@EditedEntityContainer("dicGuardianTypeDc")
@LoadDataBeforeShow
public class DicGuardianTypeEdit extends StandardEditor<DicGuardianType> {
}