package kz.uco.tsadv.web.modules.learning.course;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.learning.model.Course;
import kz.uco.tsadv.modules.learning.model.CourseSectionAttempt;
import kz.uco.tsadv.modules.learning.model.Enrollment;
import kz.uco.tsadv.modules.learning.model.ForMassEnrollment;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class EnrollmentsForCourse extends AbstractWindow {
    @Inject
    protected Metadata metadata;
    Map<String, Object> param;
    @Inject
    protected Button editButton;
    @Inject
    protected Label courseNameLabel;
    @Inject
    protected CollectionDatasource<Enrollment, UUID> enrollmentsDs;
    @Inject
    protected Button removeButton;
    @Inject
    protected DataGrid<Enrollment> enrollmentsDataGrid;
    @Inject
    protected CommonService commonService;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        param = params;
        setCaptionAsCourseName(params);
        enrollmentsDataGrid.addSelectionListener(event -> {
            enrollmentsDataGridSelectionListener(event);
        });
        editButton.setEnabled(false);
    }

    protected void setCaptionAsCourseName(Map<String, Object> params) {
        Course course = (Course) params.get("courseId");
        if (course != null && course.getName() != null) {
            int courseNameLengthInBreadCrumbsPath = 45;
            if (course.getName().length() > courseNameLengthInBreadCrumbsPath)
            setCaption(StringUtils.left(course.getName(), courseNameLengthInBreadCrumbsPath) + " ...");
            courseNameLabel.setValue(
                    getMessage("CourseSection.course") + ": " +
                    course.getName());
            courseNameLabel.setVisible(true);
        }
    }

    protected void enrollmentsDataGridSelectionListener(DataGrid.SelectionEvent<Enrollment> event) {
        List<CourseSectionAttempt> attempts = commonService.getEntities(CourseSectionAttempt.class,
                "select e from tsadv$CourseSectionAttempt e where e.deleteTs is null",
                null, "courseSectionAttempt.for.check.on.remove");
        removeButton.setEnabled(true);
        if (!enrollmentsDataGrid.getSelected().isEmpty()) {
            Set<Enrollment> enrollments = enrollmentsDataGrid.getSelected();
            for (Enrollment enrollment : enrollments) {
                for (CourseSectionAttempt attempt : attempts) {
                    if (attempt.getEnrollment().getId().equals(enrollment.getId())) {
                        removeButton.setEnabled(false);
                        break;
                    }
                }
                if (!removeButton.isEnabled())
                    break;
            }
        }


        editButton.setEnabled(enrollmentsDataGrid.getSingleSelected() != null &&
                enrollmentsDataGrid.getSelected().size() == 1);
    }

    public void onCreateButtonClick() {
        Enrollment enrollment = metadata.create(Enrollment.class);
        enrollment.setCourse(param.get("courseId") != null ? (Course) param.get("courseId") : null);
        enrollment.setDate(CommonUtils.getSystemDate());
        AbstractEditor abstractEditor = openEditor("tsadv$Enrollment.single.for.course.edit", enrollment,
                WindowManager.OpenType.THIS_TAB, ParamsMap.of("create", null,
                        "courseId", param.get("courseId")));
        abstractEditor.addCloseListener(actionId -> {
            enrollmentsDs.refresh();
            enrollmentsDataGrid.repaint();
        });

    }

    public void onMassEnrollBtnClick() {
        ForMassEnrollment massEnrollment = metadata.create(ForMassEnrollment.class);
        massEnrollment.setCourse((Course)(param.containsKey("courseId") ?
                param.get("courseId") : null));

        AbstractEditor abstractEditor = openEditor("massenrollmentscreen", massEnrollment,
                WindowManager.OpenType.THIS_TAB,
                ParamsMap.of("courseId", param.containsKey("courseId") ?
                        param.get("courseId") : null));
        abstractEditor.addCloseListener(actionId -> {
            abstractEditorCloseListener(actionId);
        });

    }

    protected void abstractEditorCloseListener(String actionId) {
        enrollmentsDs.refresh();
        enrollmentsDataGrid.repaint();
    }

    public void onEditButtonClick() {
        if (enrollmentsDataGrid.getSingleSelected() != null) {
            Enrollment singleSelected = enrollmentsDataGrid.getSingleSelected();
            openEditor("tsadv$Enrollment.single.for.course.edit",
                    singleSelected, WindowManager.OpenType.THIS_TAB,
                    ParamsMap.of("create", null));
            enrollmentsDs.refresh();
        }

    }
}