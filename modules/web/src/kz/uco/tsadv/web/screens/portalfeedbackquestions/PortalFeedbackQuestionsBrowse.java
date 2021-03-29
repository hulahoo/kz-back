package kz.uco.tsadv.web.screens.portalfeedbackquestions;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.learning.model.PortalFeedbackQuestions;

@UiController("tsadv_PortalFeedbackQuestions.browse")
@UiDescriptor("portal-feedback-questions-browse.xml")
@LookupComponent("portalFeedbackQuestionsesTable")
@LoadDataBeforeShow
public class PortalFeedbackQuestionsBrowse extends StandardLookup<PortalFeedbackQuestions> {
}