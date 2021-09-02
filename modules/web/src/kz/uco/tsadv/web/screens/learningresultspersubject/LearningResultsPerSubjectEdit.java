package kz.uco.tsadv.web.screens.learningresultspersubject;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.LearningResultsPerSubject;

@UiController("tsadv_LearningResultsPerSubject.edit")
@UiDescriptor("learning-results-per-subject-edit.xml")
@EditedEntityContainer("learningResultsPerSubjectDc")
@LoadDataBeforeShow
public class LearningResultsPerSubjectEdit extends StandardEditor<LearningResultsPerSubject> {
}