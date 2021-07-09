package kz.uco.tsadv.web.screens.dictypeoftraining;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.learning.dictionary.DicTypeOfTraining;

@UiController("tsadv_DicTypeOfTraining.browse")
@UiDescriptor("dic-type-of-training-browse.xml")
@LookupComponent("dicTypeOfTrainingsTable")
@LoadDataBeforeShow
public class DicTypeOfTrainingBrowse extends StandardLookup<DicTypeOfTraining> {
}