package kz.uco.tsadv.web.modules.learning.enrollment;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import kz.uco.tsadv.modules.learning.model.CourseSectionAttempt;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

public class AssignedTestAttempts extends AbstractWindow {

    @Inject
    protected CollectionDatasource<CourseSectionAttempt, UUID> courseSectionAttemptsDs;

    @Inject
    protected DataManager dataManager;

    @Inject
    protected Button showAnswers;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        showAnswers.setEnabled(false);
        courseSectionAttemptsDs.addItemChangeListener(e -> showAnswers.setEnabled(e.getItem() != null));
    }

    public void showAnswers() {
        openWindow("assigned-test-attempt-answers",
                WindowManager.OpenType.DIALOG,
                ParamsMap.of(AssignedTestAttemptAnswers.ATTEMPT, courseSectionAttemptsDs.getItem()));
    }

}