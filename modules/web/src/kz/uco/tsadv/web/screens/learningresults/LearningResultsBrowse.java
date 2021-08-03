package kz.uco.tsadv.web.screens.learningresults;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.LearningResults;

@UiController("tsadv_LearningResult.browse")
@UiDescriptor("learning-results-browse.xml")
@LookupComponent("learningResultsTable")
@LoadDataBeforeShow
public class LearningResultsBrowse extends StandardLookup<LearningResults> {
}