package kz.uco.tsadv.web.screens.positioncourse;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.learning.model.PositionCourse;

@UiController("tsadv_PositionCourse.edit")
@UiDescriptor("position-course-edit.xml")
@EditedEntityContainer("positionCourseDc")
@LoadDataBeforeShow
public class PositionCourseEdit extends StandardEditor<PositionCourse> {
}