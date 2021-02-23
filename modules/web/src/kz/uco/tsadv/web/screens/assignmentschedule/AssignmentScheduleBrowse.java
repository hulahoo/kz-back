package kz.uco.tsadv.web.screens.assignmentschedule;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.timesheet.model.AssignmentSchedule;

@UiController("tsadv$AssignmentScheduleLite.browse")
@UiDescriptor("assignment-schedule-lite-browse.xml")
@LookupComponent("assignmentSchedulesTable")
@LoadDataBeforeShow
public class AssignmentScheduleBrowse extends StandardLookup<AssignmentSchedule> {
}