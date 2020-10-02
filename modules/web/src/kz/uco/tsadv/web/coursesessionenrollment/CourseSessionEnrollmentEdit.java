package kz.uco.tsadv.web.coursesessionenrollment;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.PickerField;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.learning.enums.EnrollmentStatus;
import kz.uco.tsadv.modules.learning.model.Course;
import kz.uco.tsadv.modules.learning.model.CourseSessionEnrollment;

import javax.inject.Named;
import java.util.Map;

public class CourseSessionEnrollmentEdit extends AbstractEditor<CourseSessionEnrollment> {

    public static final String PARAMETER_COURSE = "course";

    @WindowParam(name = PARAMETER_COURSE)
    protected Course course;

    @Named("fieldGroup.enrollment")
    protected PickerField enrollmentField;

    @Override
    protected void initNewItem(CourseSessionEnrollment item) {
        super.initNewItem(item);

        item.setEnrollmentDate(CommonUtils.getSystemDate());
        item.setStatus(EnrollmentStatus.APPROVED);
    }

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        customizeEnrollmentFieldLookupAction(params);
    }

    protected void customizeEnrollmentFieldLookupAction(Map<String, Object> params) {
        PickerField.LookupAction lookupAction = enrollmentField.getLookupAction();
        if (lookupAction != null) {
            lookupAction.setLookupScreen("tsadv$EnrollmentPerson.lookup");
            lookupAction.setLookupScreenParams(
                    ParamsMap.of(
                            "fromSingleCourseSection", null,
                            PARAMETER_COURSE, course,
                            "courseId", params.get("courseId"),
                            "enrollmentIds", params.get("enrollmentIds")));
        }
    }
}