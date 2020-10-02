package kz.uco.tsadv.web.coursesectionsession;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.PickerField;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.Datasource;
import kz.uco.tsadv.modules.learning.model.CourseSectionSession;

import javax.inject.Inject;
import javax.inject.Named;
import kz.uco.tsadv.modules.performance.model.Trainer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MyCourseSectionSessionEdit extends AbstractEditor<CourseSectionSession> {

    @Named("courseSectionSessionTable.create")
    private CreateAction courseSectionSessionTableCreate;

    @Inject
    private Datasource<CourseSectionSession> courseSectionSessionDs;

    @Named("courseSectionSessionTable.edit")
    private EditAction courseSectionSessionTableEdit;
    @Named("fieldGroup.courseSection")
    private PickerField courseSectionField;

    @Override
    protected void postInit() {
        super.postInit();
        List<UUID> enrollmentIdList = new ArrayList<>();
        if (getItem().getTrainer() != null) {
            setLookupParams(getItem().getTrainer().getId());
        }else {
            setLookupParams(null);
        }
        courseSectionSessionDs.addItemPropertyChangeListener(e -> {
            if ("trainer".equals(e.getProperty())) {
                if (e.getValue() != null) {
                    setLookupParams(((Trainer) e.getValue()).getId());
                }else {
                    setLookupParams(null);
                }
            }
        });
        courseSectionField.setVisible(true);
        courseSectionSessionTableCreate.setBeforeActionPerformedHandler(() -> {
            enrollmentIdList.clear();
            if (getItem().getCourseSessionEnrollmentList() != null) {
                getItem().getCourseSessionEnrollmentList().forEach(courseSessionEnrollment -> {
                    enrollmentIdList.add(courseSessionEnrollment.getEnrollment().getId());
                });
            }

            UUID courseId = null;
            if (getItem().getCourseSection() != null) {
                courseId = getItem().getCourseSection().getCourse().getId();
            }
            courseSectionSessionTableCreate.setWindowParams(ParamsMap.of("courseSession", getItem(),
                    "enrollmentIds", enrollmentIdList,
                    "courseId", courseId));
            return true;
        });

        courseSectionSessionTableEdit.setBeforeActionPerformedHandler(() -> {
            enrollmentIdList.clear();
            if (getItem().getCourseSessionEnrollmentList() != null) {
                getItem().getCourseSessionEnrollmentList().forEach(courseSessionEnrollment -> {
                    enrollmentIdList.add(courseSessionEnrollment.getEnrollment().getId());
                });
            }
            UUID courseId = null;
            if (getItem().getCourseSection() != null) {
                courseId = getItem().getCourseSection().getCourse().getId();
            }
            courseSectionSessionTableEdit.setWindowParams(ParamsMap.of("enrollmentIds", enrollmentIdList,
                    "courseId", courseId));
            return true;
        });


    }

    private void setLookupParams(UUID trainerId) {
        courseSectionField.getLookupAction().setLookupScreenParams(
                ParamsMap.of("fromCourseSectionSessionEdit", true,
                        "trainerId", trainerId));
    }
}