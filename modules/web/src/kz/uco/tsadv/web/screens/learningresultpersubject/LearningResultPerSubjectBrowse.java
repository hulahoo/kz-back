package kz.uco.tsadv.web.screens.learningresultpersubject;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.LearningResultPerSubject;

@UiController("tsadv_LearningResultPerSubject.browse")
@UiDescriptor("learning-result-per-subject-browse.xml")
@LookupComponent("learningResultPerSubjectsTable")
@LoadDataBeforeShow
public class LearningResultPerSubjectBrowse extends StandardLookup<LearningResultPerSubject> {
}