package kz.uco.tsadv.web.modules.performance.performanceplan;

import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.Screens;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.model.InstanceLoader;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.performance.model.AssignedPerformancePlan;
import kz.uco.tsadv.modules.performance.model.InstructionsKpi;
import kz.uco.tsadv.modules.performance.model.PerformancePlan;

import javax.inject.Inject;
import java.util.Date;

@UiController("tsadv$PerformancePlan.edit")
@UiDescriptor("performance-plan-edit.xml")
@EditedEntityContainer("performancePlanDc")
public class PerformancePlanEdit extends StandardEditor<PerformancePlan> {
    @Inject
    protected Notifications notifications;
    @Inject
    protected MessageBundle messageBundle;
    @Inject
    protected Screens screens;
    @Inject
    protected InstanceLoader<PerformancePlan> performancePlanDl;
    @Inject
    protected CollectionLoader<AssignedPerformancePlan> assignedPerformancePlansDl;
    @Inject
    protected InstanceContainer<PerformancePlan> performancePlanDc;
    @Inject
    protected CollectionLoader<InstructionsKpi> instructionKpiDl;

    @Subscribe
    protected void onBeforeShow(BeforeShowEvent event) {
        performancePlanDl.load();
        assignedPerformancePlansDl.setParameter("performancePlan", performancePlanDc.getItem());
        assignedPerformancePlansDl.load();
        instructionKpiDl.setParameter("performancePlan", performancePlanDc.getItem());
        instructionKpiDl.load();
    }

    @Subscribe
    protected void onBeforeCommitChanges(BeforeCommitChangesEvent event) {
        Date accessibilityStartDate = performancePlanDc.getItem().getAccessibilityStartDate();
        Date startDate = performancePlanDc.getItem().getStartDate();
        Date accessibilityEndDate = performancePlanDc.getItem().getAccessibilityEndDate();
        Date endDate = performancePlanDc.getItem().getEndDate();
        if (accessibilityStartDate != null && startDate != null && accessibilityStartDate.before(startDate)) {
            notifications.create().withPosition(Notifications.Position.BOTTOM_RIGHT)
                    .withCaption(messageBundle.getMessage("accessStartDateNotBeEarlier")).show();
        }
        if (accessibilityEndDate != null && endDate != null && accessibilityEndDate.after(endDate)) {
            notifications.create().withPosition(Notifications.Position.BOTTOM_RIGHT)
                    .withCaption(messageBundle.getMessage("accessEndDateNotBeAfter")).show();
        }
    }
}