package kz.uco.tsadv.web.screens.portalfeedbackquestions;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.gui.export.ExportDisplay;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.learning.model.PortalFeedbackQuestions;

import javax.inject.Inject;

@UiController("tsadv_PortalFeedbackQuestions.edit")
@UiDescriptor("portal-feedback-questions-edit.xml")
@EditedEntityContainer("portalFeedbackQuestionsDc")
@LoadDataBeforeShow
public class PortalFeedbackQuestionsEdit extends StandardEditor<PortalFeedbackQuestions> {

    @Inject
    protected ExportDisplay exportDisplay;

    public void downloadFile(FileDescriptor item, String columnId) {
        exportDisplay.show(item);
    }
}