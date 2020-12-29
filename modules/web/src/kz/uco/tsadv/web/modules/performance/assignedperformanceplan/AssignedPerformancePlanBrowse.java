package kz.uco.tsadv.web.modules.performance.assignedperformanceplan;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.performance.model.AssignedPerformancePlan;

@UiController("tsadv$AssignedPerformancePlan.browse")
@UiDescriptor("assigned-performance-plan-browse.xml")
@LookupComponent("assignedPerformancePlansTable")
@LoadDataBeforeShow
public class AssignedPerformancePlanBrowse extends StandardLookup<AssignedPerformancePlan> {
}