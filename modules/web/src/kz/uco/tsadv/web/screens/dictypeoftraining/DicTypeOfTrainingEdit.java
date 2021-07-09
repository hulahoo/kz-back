package kz.uco.tsadv.web.screens.dictypeoftraining;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.learning.dictionary.DicTypeOfTraining;

@UiController("tsadv_DicTypeOfTraining.edit")
@UiDescriptor("dic-type-of-training-edit.xml")
@EditedEntityContainer("dicTypeOfTrainingDc")
@LoadDataBeforeShow
public class DicTypeOfTrainingEdit extends StandardEditor<DicTypeOfTraining> {
}