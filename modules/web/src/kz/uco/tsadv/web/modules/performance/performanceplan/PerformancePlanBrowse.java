package kz.uco.tsadv.web.modules.performance.performanceplan;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.performance.model.PerformancePlan;

@UiController("tsadv$PerformancePlan.browse")
@UiDescriptor("performance-plan-browse.xml")
@LookupComponent("performancePlansTable")
@LoadDataBeforeShow
public class PerformancePlanBrowse extends StandardLookup<PerformancePlan> {
}