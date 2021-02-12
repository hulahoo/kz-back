package kz.uco.tsadv.web.screens.courseschedule;

import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.learning.model.CourseSchedule;

import javax.inject.Inject;

@UiController("tsadv_CourseSchedule.browse")
@UiDescriptor("course-schedule-browse.xml")
@LookupComponent("courseSchedulesTable")
@LoadDataBeforeShow
public class CourseScheduleBrowse extends StandardLookup<CourseSchedule> {
    @Inject
    protected CollectionLoader<CourseSchedule> courseSchedulesDl;

    @Subscribe
    protected void onInit(InitEvent event) {
        if (event != null) {
            try {
                MapScreenOptions mapScreenOptions = (MapScreenOptions) event.getOptions();
                if (mapScreenOptions.getParams().containsKey("course")) {
                    courseSchedulesDl.setQuery("select e from tsadv_CourseSchedule e " +
                            " where e.course = :course ");
                    courseSchedulesDl.setParameter("course", mapScreenOptions.getParams().get("course"));
                    courseSchedulesDl.load();
                }
            } catch (Exception ignored) {
            }
        }
    }

}