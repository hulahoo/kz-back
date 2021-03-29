package kz.uco.tsadv.web.screens.portalfeedbackquestions;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.learning.model.PortalFeedbackQuestions;

@UiController("tsadv_PortalFeedbackQuestions.edit")
@UiDescriptor("portal-feedback-questions-edit.xml")
@EditedEntityContainer("portalFeedbackQuestionsDc")
@LoadDataBeforeShow
public class PortalFeedbackQuestionsEdit extends StandardEditor<PortalFeedbackQuestions> {
}