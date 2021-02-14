package kz.uco.tsadv.web.screens.courseschedule;

import com.haulmont.cuba.gui.components.HasValue;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.learning.dictionary.DicLearningCenter;
import kz.uco.tsadv.modules.learning.model.CourseSchedule;

import javax.inject.Inject;

@UiController("tsadv_CourseSchedule.edit")
@UiDescriptor("course-schedule-edit.xml")
@EditedEntityContainer("courseScheduleDc")
@LoadDataBeforeShow
public class CourseScheduleEdit extends StandardEditor<CourseSchedule> {

    @Inject
    protected InstanceContainer<CourseSchedule> courseScheduleDc;

    @Subscribe("learningCenterField")
    protected void onLearningCenterFieldValueChange(HasValue.ValueChangeEvent<DicLearningCenter> event) {
        if (event != null && event.getValue() != null) {
            courseScheduleDc.getItem().setAddress(event.getValue().getAddress());
        }
    }
}