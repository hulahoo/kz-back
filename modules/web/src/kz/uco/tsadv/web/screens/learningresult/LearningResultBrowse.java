package kz.uco.tsadv.web.screens.learningresult;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.LearningResult;

@UiController("tsadv_LearningResult.browse")
@UiDescriptor("learning-result-browse.xml")
@LookupComponent("learningResultsTable")
@LoadDataBeforeShow
public class LearningResultBrowse extends StandardLookup<LearningResult> {
}