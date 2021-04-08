package kz.uco.tsadv.web.modules.performance.assignedperformanceplan;

import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.HBoxLayout;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.performance.dictionary.DicPerformanceStage;
import kz.uco.tsadv.modules.performance.model.AssignedPerformancePlan;
import kz.uco.tsadv.modules.personal.dictionary.DicRequestStatus;

import javax.inject.Inject;
import java.util.Set;

@UiController("tsadv$AssignedPerformancePlanForChangeStatus.edit")
@UiDescriptor("assigned-performance-plan-for-change-status-edit.xml")
@EditedEntityContainer("assignedPerformancePlanDc")
@LoadDataBeforeShow
public class AssignedPerformancePlanForChangeStatusEdit extends StandardEditor<AssignedPerformancePlan> {

    protected Set<AssignedPerformancePlan> assignedPerformancePlans;
    @Inject
    protected LookupField<DicRequestStatus> statusField;
    @Inject
    protected LookupField<DicPerformanceStage> stageField;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected InstanceContainer<AssignedPerformancePlan> assignedPerformancePlanDc;
    @Inject
    protected HBoxLayout editActions;

    public void setParameter(Set<AssignedPerformancePlan> assignedPerformancePlans) {
        this.assignedPerformancePlans = assignedPerformancePlans;
    }

    @Subscribe("ok")
    protected void onOkClick(Button.ClickEvent event) {
        CommitContext commitContext = new CommitContext();
        assignedPerformancePlans.forEach(assignedPerformancePlan -> {
            assignedPerformancePlan.setStatus(statusField.getValue());
            assignedPerformancePlan.setStage(stageField.getValue());
            commitContext.addInstanceToCommit(assignedPerformancePlan);
        });
//        commitContext.addInstanceToRemove(assignedPerformancePlanDc.getItem());
        dataManager.commit(commitContext);
        closeWithDefaultAction();
    }
}