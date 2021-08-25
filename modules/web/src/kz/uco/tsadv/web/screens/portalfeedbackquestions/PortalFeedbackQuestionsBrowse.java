package kz.uco.tsadv.web.screens.portalfeedbackquestions;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.modules.learning.model.PortalFeedbackQuestions;

import javax.inject.Inject;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@UiController("tsadv_PortalFeedbackQuestions.browse")
@UiDescriptor("portal-feedback-questions-browse.xml")
@LookupComponent("portalFeedbackQuestionsesTable")
@LoadDataBeforeShow
public class PortalFeedbackQuestionsBrowse extends StandardLookup<PortalFeedbackQuestions> {
}