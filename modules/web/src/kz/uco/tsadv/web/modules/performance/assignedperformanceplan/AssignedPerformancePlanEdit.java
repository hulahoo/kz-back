package kz.uco.tsadv.web.modules.performance.assignedperformanceplan;

import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.performance.model.AssignedPerformancePlan;

import javax.inject.Inject;


@UiController("tsadv$AssignedPerformancePlan.edit")
@UiDescriptor("assigned-performance-plan-edit.xml")
@EditedEntityContainer("assignedPerformancePlanDc")
public class AssignedPerformancePlanEdit extends StandardEditor<AssignedPerformancePlan> {

    @Inject
    protected InstanceContainer<AssignedPerformancePlan> assignedPerformancePlanDc;

    @Subscribe
    protected void onAfterShow(AfterShowEvent event) {
        assignedPerformancePlanDc.setItem(getEditedEntity());
    }


}