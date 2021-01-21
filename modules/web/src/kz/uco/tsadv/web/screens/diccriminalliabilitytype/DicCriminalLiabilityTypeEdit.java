package kz.uco.tsadv.web.screens.diccriminalliabilitytype;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.dictionary.DicCriminalLiabilityType;

@UiController("tsadv_DicCriminalLiabilityType.edit")
@UiDescriptor("dic-criminal-liability-type-edit.xml")
@EditedEntityContainer("dicCriminalLiabilityTypeDc")
@LoadDataBeforeShow
public class DicCriminalLiabilityTypeEdit extends StandardEditor<DicCriminalLiabilityType> {
}