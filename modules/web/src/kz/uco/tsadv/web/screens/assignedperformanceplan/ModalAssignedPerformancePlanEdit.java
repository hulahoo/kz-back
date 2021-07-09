package kz.uco.tsadv.web.screens.assignedperformanceplan;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.performance.model.AssignedPerformancePlan;

@UiController("tsadv$ModalAssignedPerformancePlan.edit")
@UiDescriptor("modal-assigned-performance-plan-edit.xml")
@EditedEntityContainer("assignedPerformancePlanDc")
@LoadDataBeforeShow
public class ModalAssignedPerformancePlanEdit extends StandardEditor<AssignedPerformancePlan> {
}