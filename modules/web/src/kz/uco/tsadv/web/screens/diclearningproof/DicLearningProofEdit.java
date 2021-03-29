package kz.uco.tsadv.web.screens.diclearningproof;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.learning.dictionary.DicLearningProof;

@UiController("tsadv_DicLearningProof.edit")
@UiDescriptor("dic-learning-proof-edit.xml")
@EditedEntityContainer("dicLearningProofDc")
@LoadDataBeforeShow
public class DicLearningProofEdit extends StandardEditor<DicLearningProof> {
}