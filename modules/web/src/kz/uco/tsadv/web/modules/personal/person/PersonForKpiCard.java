package kz.uco.tsadv.web.modules.personal.person;

import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.components.DataGrid;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.learning.model.Course;
import kz.uco.tsadv.modules.learning.model.CourseSchedule;
import kz.uco.tsadv.modules.personal.model.PersonExt;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@UiController("base$PersonForKpiCard.browse")
@UiDescriptor("person-for-kpi-card.xml")
@LookupComponent("dataGrid")
@LoadDataBeforeShow
public class PersonForKpiCard extends StandardLookup<PersonExt> {
    @Inject
    protected DataGrid<PersonExt> dataGrid;

    protected Course course = null;

    protected List<CourseSchedule> courseSchedules = new ArrayList<>();
    @Inject
    protected Metadata metadata;
    @Inject
    protected ScreenBuilders screenBuilders;


    @Subscribe
    protected void onInit(InitEvent event) {
        if (event != null) {
            try {
                MapScreenOptions options = (MapScreenOptions) event.getOptions();
                if (options.getParams().containsKey("course") && options.getParams().containsKey("courseSchedules")) {
                    course = (Course) options.getParams().get("course");
                    courseSchedules = (List<CourseSchedule>) options.getParams().get("courseSchedules");
                }
            } catch (Exception ignored) {
            }
        }
    }
}