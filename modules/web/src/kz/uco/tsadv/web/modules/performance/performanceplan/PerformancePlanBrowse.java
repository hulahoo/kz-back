package kz.uco.tsadv.web.modules.performance.performanceplan;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.actions.list.RemoveAction;
import com.haulmont.cuba.gui.components.GroupTable;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.performance.model.AssignedPerformancePlan;
import kz.uco.tsadv.modules.performance.model.PerformancePlan;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

@UiController("tsadv$PerformancePlan.browse")
@UiDescriptor("performance-plan-browse.xml")
@LookupComponent("performancePlansTable")
@LoadDataBeforeShow
public class PerformancePlanBrowse extends StandardLookup<PerformancePlan> {

    @Inject
    protected GroupTable<PerformancePlan> performancePlansTable;
    @Inject
    protected DataManager dataManager;
    @Named("performancePlansTable.remove")
    protected RemoveAction<PerformancePlan> performancePlansTableRemove;

    @Subscribe
    protected void onInit(InitEvent event) {
        performancePlansTable.addSelectionListener(performancePlanSelectionEvent -> {
            if (performancePlanSelectionEvent != null && performancePlanSelectionEvent.getSelected().size() > 0) {
                List<AssignedPerformancePlan> assignedPerformancePlanList = dataManager.load(AssignedPerformancePlan.class)
                        .query("select e from tsadv$AssignedPerformancePlan e " +
                                " where e.performancePlan = :performancePlan ")
                        .parameter("performancePlan", performancePlanSelectionEvent.getSelected().iterator().next())
                        .view("assignedPerformancePlan.browse")
                        .list();
                if (!assignedPerformancePlanList.isEmpty()) {
                    performancePlansTableRemove.setEnabled(false);
                } else {
                    performancePlansTableRemove.setEnabled(true);
                }
            }
        });
    }
}