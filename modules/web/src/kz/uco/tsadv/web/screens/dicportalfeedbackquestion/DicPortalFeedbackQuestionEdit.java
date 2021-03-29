package kz.uco.tsadv.web.screens.dicportalfeedbackquestion;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.learning.dictionary.DicPortalFeedbackQuestion;

@UiController("tsadv_DicPortalFeedbackQuestion.edit")
@UiDescriptor("dic-portal-feedback-question-edit.xml")
@EditedEntityContainer("dicPortalFeedbackQuestionDc")
@LoadDataBeforeShow
public class DicPortalFeedbackQuestionEdit extends StandardEditor<DicPortalFeedbackQuestion> {
}