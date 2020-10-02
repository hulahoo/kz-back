package kz.uco.tsadv.web.modules.learning.coursefeedbackpersonanswer;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.tsadv.modules.learning.model.feedback.CourseFeedbackPersonAnswer;
import kz.uco.tsadv.web.coursefeedbackpersonanswerdetail.CourseFeedbackPersonAnswerDetailSimple;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

public class CourseFeedbackPersonAnswerBrowse extends AbstractLookup {

    @Inject
    private Button showAnswersBtn;

    @Inject
    private GroupDatasource<CourseFeedbackPersonAnswer, UUID> courseFeedbackPersonAnswersDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        courseFeedbackPersonAnswersDs.addItemChangeListener(e -> showAnswersBtn.setEnabled(e.getItem() != null));
    }

    public void showAnswers() {
        CourseFeedbackPersonAnswer personAnswer = courseFeedbackPersonAnswersDs.getItem();

        if (personAnswer != null) {
            openWindow("tsadv$CourseFeedbackPersonAnswerDetailSimple.browse",
                    WindowManager.OpenType.DIALOG,
                    ParamsMap.of(CourseFeedbackPersonAnswerDetailSimple.COURSE_FEEDBACK_PERSON_ANSWER_ID, personAnswer.getId()));
        } else {
            showNotification("CourseFeedbackPersonAnswer not selected!", NotificationType.TRAY);
        }

    }
}