package kz.uco.tsadv.web.modules.performance.performanceplan;

import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.Screens;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.TabSheet;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.model.InstanceLoader;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.base.common.BaseCommonUtils;
import kz.uco.tsadv.modules.performance.enums.CardStatusEnum;
import kz.uco.tsadv.modules.performance.model.AssignedPerformancePlan;
import kz.uco.tsadv.modules.performance.model.InstructionsKpi;
import kz.uco.tsadv.modules.performance.model.PerformancePlan;
import kz.uco.tsadv.modules.personal.model.PersonExt;

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
    @Inject
    protected ScreenBuilders screenBuilders;
    @Inject
    protected Metadata metadata;
    @Inject
    protected CollectionContainer<AssignedPerformancePlan> assignedPerformancePlansDc;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected TabSheet tabSheet;
    @Inject
    protected Table<InstructionsKpi> instructionTable;
    @Inject
    protected Table<AssignedPerformancePlan> assignedPerformancePlanTable;

    @Subscribe
    protected void onBeforeShow(BeforeShowEvent event) {
        performancePlanDl.load();
        assignedPerformancePlansDl.setParameter("performancePlan", performancePlanDc.getItem());
        assignedPerformancePlansDl.load();
        instructionKpiDl.setParameter("performancePlan", performancePlanDc.getItem());
        instructionKpiDl.load();
    }

    @Subscribe
    protected void onAfterShow(AfterShowEvent event) {
        if (PersistenceHelper.isNew(performancePlanDc.getItem())) {
            visibleTab(false);
            performancePlanDc.getItem().setStartDate(BaseCommonUtils.getSystemDate());
            performancePlanDc.getItem().setEndDate(BaseCommonUtils.getEndOfTime());
            performancePlanDc.getItem().setAccessibilityStartDate(BaseCommonUtils.getSystemDate());
            performancePlanDc.getItem().setAccessibilityEndDate(BaseCommonUtils.getEndOfTime());
        } else {
            visibleTab(true);
        }
    }

    @Subscribe
    protected void onAfterCommitChanges(AfterCommitChangesEvent event) {
        visibleTab(true);
    }

    protected void visibleTab(Boolean isVisible) {
        tabSheet.getTab("assignedPerformancePlan").setVisible(isVisible);
        tabSheet.getTab("instruction").setVisible(isVisible);
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
            event.preventCommit();
        }
        if (accessibilityEndDate != null && endDate != null && accessibilityEndDate.after(endDate)) {
            notifications.create().withPosition(Notifications.Position.BOTTOM_RIGHT)
                    .withCaption(messageBundle.getMessage("accessEndDateNotBeAfter")).show();
            event.preventCommit();
        }
    }

    @Subscribe("instructionTable.create")
    protected void onInstructionTableCreate(Action.ActionPerformedEvent event) {
        screenBuilders.editor(instructionTable)
                .newEntity()
                .withInitializer(instructionsKpi -> instructionsKpi.setPerformancePlan(performancePlanDc.getItem()))
                .build().show()
                .addAfterCloseListener(afterCloseEvent -> instructionKpiDl.load());
    }

    @Subscribe("assignedPerformancePlanTable.create")
    protected void onAssignedPerformancePlanTableCreate(Action.ActionPerformedEvent event) {
        screenBuilders.lookup(PersonExt.class, this)
                .withScreenId("base$PersonForKpiCard.browse")
                .withSelectHandler(personList -> {
                    CommitContext commitContext = new CommitContext();
                    personList.forEach(personExt -> {
                        boolean isNew = true;
                        for (AssignedPerformancePlan item : assignedPerformancePlansDc.getItems()) {
                            if (item.getAssignedPerson().equals(personExt.getGroup())) {
                                isNew = false;
                                break;
                            }
                        }
                        if (isNew) {
                            AssignedPerformancePlan assignedPerformancePlan = metadata
                                    .create(AssignedPerformancePlan.class);
                            assignedPerformancePlan.setPerformancePlan(performancePlanDc.getItem());
                            assignedPerformancePlan.setAssignedPerson(personExt.getGroup());
                            assignedPerformancePlan.setStatus(CardStatusEnum.DRAFT);
                            assignedPerformancePlan.setStartDate(performancePlanDc.getItem().getStartDate());
                            assignedPerformancePlan.setEndDate(performancePlanDc.getItem().getEndDate());
                            commitContext.addInstanceToCommit(assignedPerformancePlan);
                        }
                    });
                    dataManager.commit(commitContext);
                    assignedPerformancePlansDl.load();
                })
                .build().show();
    }
}