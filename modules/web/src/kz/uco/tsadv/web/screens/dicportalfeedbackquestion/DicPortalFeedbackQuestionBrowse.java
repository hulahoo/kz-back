package kz.uco.tsadv.web.screens.dicportalfeedbackquestion;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.learning.dictionary.DicPortalFeedbackQuestion;

@UiController("tsadv_DicPortalFeedbackQuestion.browse")
@UiDescriptor("dic-portal-feedback-question-browse.xml")
@LookupComponent("dicPortalFeedbackQuestionsTable")
@LoadDataBeforeShow
public class DicPortalFeedbackQuestionBrowse extends StandardLookup<DicPortalFeedbackQuestion> {
}