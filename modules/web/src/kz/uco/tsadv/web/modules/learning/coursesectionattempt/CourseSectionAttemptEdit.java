package kz.uco.tsadv.web.modules.learning.coursesectionattempt;

import com.haulmont.cuba.gui.components.AbstractEditor;
import kz.uco.tsadv.modules.learning.model.CourseSectionAttempt;
import kz.uco.tsadv.service.CourseService;

import javax.inject.Inject;

public class CourseSectionAttemptEdit extends AbstractEditor<CourseSectionAttempt> {

    @Inject
    private CourseService courseService;

    @Override
    protected boolean postCommit(boolean committed, boolean close) {
        if (committed) {
            courseService.updateEnrollmentStatus(getItem());
        }
        return super.postCommit(committed, close);
    }
}