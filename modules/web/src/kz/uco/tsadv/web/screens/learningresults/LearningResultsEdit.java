package kz.uco.tsadv.web.screens.learningresults;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.LearningResults;

@UiController("tsadv_LearningResults.edit")
@UiDescriptor("learning-results-edit.xml")
@EditedEntityContainer("learningResultsDc")
@LoadDataBeforeShow
public class LearningResultsEdit extends StandardEditor<LearningResults> {
}