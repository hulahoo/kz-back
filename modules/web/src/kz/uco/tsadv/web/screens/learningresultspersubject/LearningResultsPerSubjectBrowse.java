package kz.uco.tsadv.web.screens.learningresultspersubject;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.LearningResultsPerSubject;

@UiController("tsadv_LearningResultsPerSubject.browse")
@UiDescriptor("learning-results-per-subject-browse.xml")
@LookupComponent("learningResultsPerSubjectsTable")
@LoadDataBeforeShow
public class LearningResultsPerSubjectBrowse extends StandardLookup<LearningResultsPerSubject> {
}