package kz.uco.tsadv.web.screens.learningresultpersubject;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.LearningResultPerSubject;

@UiController("tsadv_LearningResultPerSubject.edit")
@UiDescriptor("learning-result-per-subject-edit.xml")
@EditedEntityContainer("learningResultPerSubjectDc")
@LoadDataBeforeShow
public class LearningResultPerSubjectEdit extends StandardEditor<LearningResultPerSubject> {
}