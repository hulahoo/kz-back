package kz.uco.tsadv.web.screens.diclearningproof;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.learning.dictionary.DicLearningProof;

@UiController("tsadv_DicLearningProof.browse")
@UiDescriptor("dic-learning-proof-browse.xml")
@LookupComponent("dicLearningProofsTable")
@LoadDataBeforeShow
public class DicLearningProofBrowse extends StandardLookup<DicLearningProof> {
}