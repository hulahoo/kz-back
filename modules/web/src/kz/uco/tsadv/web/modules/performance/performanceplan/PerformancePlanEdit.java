package kz.uco.tsadv.web.modules.performance.performanceplan;

import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.Screens;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.model.InstanceLoader;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.base.common.BaseCommonUtils;
import kz.uco.tsadv.modules.performance.enums.AssignedGoalTypeEnum;
import kz.uco.tsadv.modules.performance.enums.CardStatusEnum;
import kz.uco.tsadv.modules.performance.model.AssignedGoal;
import kz.uco.tsadv.modules.performance.model.AssignedPerformancePlan;
import kz.uco.tsadv.modules.performance.model.InstructionsKpi;
import kz.uco.tsadv.modules.performance.model.PerformancePlan;
import kz.uco.tsadv.modules.personal.model.*;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

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
    protected DataGrid<AssignedPerformancePlan> assignedPerformancePlanTable;

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
            performancePlanDc.getItem().setAccessibilityEndDate(BaseCommonUtils.getEndOfTime());
            performancePlanDc.getItem().setEndDate(BaseCommonUtils.getEndOfTime());
            performancePlanDc.getItem().setAccessibilityStartDate(BaseCommonUtils.getSystemDate());
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

    @Subscribe("startDate")
    protected void onStartDateValueChange(HasValue.ValueChangeEvent<Date> event) {
        Date accessibilityStartDate = performancePlanDc.getItem().getAccessibilityStartDate();
        if (accessibilityStartDate != null && event.getValue() != null
                && accessibilityStartDate.before(event.getValue())) {
            notifications.create().withPosition(Notifications.Position.BOTTOM_RIGHT)
                    .withCaption(messageBundle.getMessage("accessStartDateNotBeEarlier")).show();
        }
    }

    @Subscribe("endDate")
    protected void onEndDateValueChange(HasValue.ValueChangeEvent<Date> event) {
        Date accessibilityEndDate = performancePlanDc.getItem().getAccessibilityEndDate();
        if (accessibilityEndDate != null && event.getValue() != null && accessibilityEndDate.after(event.getValue())) {
            notifications.create().withPosition(Notifications.Position.BOTTOM_RIGHT)
                    .withCaption(messageBundle.getMessage("accessEndDateNotBeAfter")).show();
        }
    }

    @Subscribe("accessibilityStartDate")
    protected void onAccessibilityStartDateValueChange(HasValue.ValueChangeEvent<Date> event) {
        Date startDate = performancePlanDc.getItem().getStartDate();
        if (startDate != null && event.getValue() != null
                && event.getValue().before(startDate)) {
            notifications.create().withPosition(Notifications.Position.BOTTOM_RIGHT)
                    .withCaption(messageBundle.getMessage("accessStartDateNotBeEarlier")).show();
        }
    }

    @Subscribe("accessibilityEndDate")
    protected void onAccessibilityEndDateValueChange(HasValue.ValueChangeEvent<Date> event) {
        Date endDate = performancePlanDc.getItem().getEndDate();
        if (endDate != null && event.getValue() != null && event.getValue().after(endDate)) {
            notifications.create().withPosition(Notifications.Position.BOTTOM_RIGHT)
                    .withCaption(messageBundle.getMessage("accessEndDateNotBeAfter")).show();
        }
    }

    @Subscribe("assignedPerformancePlanTable.massGoals")
    protected void onAssignedPerformancePlanTableMassGoals(Action.ActionPerformedEvent event) {
        Set<AssignedPerformancePlan> assignedPerformancePlans = assignedPerformancePlanTable.getSelected();
        CommitContext commitContext = new CommitContext();
        try {
            assignedPerformancePlans.forEach(assignedPerformancePlan -> {
                AssignmentExt currentAssignment = assignedPerformancePlan.getAssignedPerson().getCurrentAssignment();
                List<OrganizationGroupGoalLink> orgGoalList = currentAssignment.getOrganizationGroup() != null
                        ? currentAssignment.getOrganizationGroup().getGoals()
                        : Collections.emptyList();
                List<PositionGroupGoalLink> positionGoalList = currentAssignment.getPositionGroup() != null
                        ? currentAssignment.getPositionGroup().getGoals()
                        : Collections.emptyList();
                List<JobGroupGoalLink> jobGoalList = currentAssignment.getJobGroup() != null
                        ? currentAssignment.getJobGroup().getGoals()
                        : Collections.emptyList();
                for (OrganizationGroupGoalLink organizationGoal : orgGoalList) {
                    AssignedGoal newAssignedGoal = metadata.create(AssignedGoal.class);
                    newAssignedGoal.setAssignedPerformancePlan(assignedPerformancePlan);
                    newAssignedGoal.setGoal(organizationGoal.getGoal());
                    newAssignedGoal.setGoalString(organizationGoal.getGoal().getGoalName());
                    newAssignedGoal.setWeight(Double.valueOf(organizationGoal.getWeight()));
                    newAssignedGoal.setOrganizationGroup(organizationGoal.getOrganizationGroup());
                    newAssignedGoal.setCategory(organizationGoal.getGoal() != null
                            && organizationGoal.getGoal().getLibrary() != null
                            ? organizationGoal.getGoal().getLibrary().getCategory()
                            : null);
                    newAssignedGoal.setGoalType(AssignedGoalTypeEnum.LIBRARY);
                    newAssignedGoal.setGoalLibrary(organizationGoal.getGoal().getLibrary());
                    commitContext.addInstanceToCommit(newAssignedGoal);
                }
                for (PositionGroupGoalLink positionGoal : positionGoalList) {
                    AssignedGoal newAssignedGoal = metadata.create(AssignedGoal.class);
                    newAssignedGoal.setAssignedPerformancePlan(assignedPerformancePlan);
                    newAssignedGoal.setGoal(positionGoal.getGoal());
                    newAssignedGoal.setGoalString(positionGoal.getGoal().getGoalName());
                    newAssignedGoal.setWeight(Double.valueOf(positionGoal.getWeight()));
                    newAssignedGoal.setPositionGroup(positionGoal.getPositionGroup());
                    newAssignedGoal.setCategory(positionGoal.getGoal() != null
                            && positionGoal.getGoal().getLibrary() != null
                            ? positionGoal.getGoal().getLibrary().getCategory()
                            : null);
                    newAssignedGoal.setGoalType(AssignedGoalTypeEnum.LIBRARY);
                    newAssignedGoal.setGoalLibrary(positionGoal.getGoal().getLibrary());
                    commitContext.addInstanceToCommit(newAssignedGoal);
                }
                for (JobGroupGoalLink jobGoal : jobGoalList) {
                    AssignedGoal newAssignedGoal = metadata.create(AssignedGoal.class);
                    newAssignedGoal.setAssignedPerformancePlan(assignedPerformancePlan);
                    newAssignedGoal.setGoal(jobGoal.getGoal());
                    newAssignedGoal.setGoalString(jobGoal.getGoal().getGoalName());
                    newAssignedGoal.setWeight(Double.valueOf(jobGoal.getWeight()));
                    newAssignedGoal.setJobGroup(jobGoal.getJobGroup());
                    newAssignedGoal.setCategory(jobGoal.getGoal() != null
                            && jobGoal.getGoal().getLibrary() != null
                            ? jobGoal.getGoal().getLibrary().getCategory()
                            : null);
                    newAssignedGoal.setGoalType(AssignedGoalTypeEnum.LIBRARY);
                    newAssignedGoal.setGoalLibrary(jobGoal.getGoal().getLibrary());
                    commitContext.addInstanceToCommit(newAssignedGoal);
                }
            });
            dataManager.commit(commitContext);
            notifications.create().withPosition(Notifications.Position.BOTTOM_RIGHT)
                    .withCaption(messageBundle.getMessage("addMassGoalSuccess")).show();
        } catch (Exception e) {
            notifications.create().withPosition(Notifications.Position.BOTTOM_RIGHT)
                    .withCaption(messageBundle.getMessage("addMassGoalNotSuccess")).show();
        }
    }
}