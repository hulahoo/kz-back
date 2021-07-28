package kz.uco.tsadv.web.modules.learning.coursefeedbackpersonanswer;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.DataGrid;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.reports.entity.Report;
import com.haulmont.reports.gui.ReportGuiManager;
import kz.uco.tsadv.modules.learning.model.feedback.CourseFeedbackPersonAnswer;
import kz.uco.tsadv.web.coursefeedbackpersonanswerdetail.CourseFeedbackPersonAnswerDetailSimple;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class CourseFeedbackPersonAnswerBrowse extends AbstractLookup {

    @Inject
    protected Button showAnswersBtn;

    @Inject
    protected GroupDatasource<CourseFeedbackPersonAnswer, UUID> courseFeedbackPersonAnswersDs;
    @Inject
    protected ReportGuiManager reportGuiManager;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected DataGrid<CourseFeedbackPersonAnswer> courseFeedbackPersonAnswersTable;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        courseFeedbackPersonAnswersDs.addItemChangeListener(e -> showAnswersBtn.setEnabled(e.getItem() != null));
    }

    public void showAnswers() {
        CourseFeedbackPersonAnswer personAnswer = courseFeedbackPersonAnswersDs.getItem();

        if (personAnswer != null) {
            openWindow("tsadv$CourseFeedbackPersonAnswerDetailSimple.browse",
                    WindowManager.OpenType.THIS_TAB,
                    ParamsMap.of(CourseFeedbackPersonAnswerDetailSimple.COURSE_FEEDBACK_PERSON_ANSWER_ID, personAnswer.getId()));
        } else {
            showNotification("CourseFeedbackPersonAnswer not selected!", NotificationType.TRAY);
        }

    }

    public void report() {
        List<CourseFeedbackPersonAnswer> courseFeedbackPersonAnswers = courseFeedbackPersonAnswersTable.getSelected()
                .stream().collect(Collectors.toList());
        if (courseFeedbackPersonAnswers != null && !courseFeedbackPersonAnswers.isEmpty()) {
            Report report = dataManager.load(Report.class)
                    .query("select e from report$Report e where e.code = :code")
                    .parameter("code", "COURSE_FEEDBACK_PERSON_ANSWER").view(View.MINIMAL)
                    .list().stream().findFirst().orElse(null);
            if (report != null) {
                reportGuiManager.printReport(report, ParamsMap.of("courseFeedbackPersonAnswer",
                        courseFeedbackPersonAnswers));
            }
        }
    }
}