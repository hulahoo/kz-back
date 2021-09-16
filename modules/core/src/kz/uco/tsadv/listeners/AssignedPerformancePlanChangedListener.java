package kz.uco.tsadv.listeners;

import com.haulmont.cuba.core.TransactionalDataManager;
import com.haulmont.cuba.core.app.events.EntityChangedEvent;
import com.haulmont.cuba.core.entity.contracts.Id;
import kz.uco.tsadv.modules.performance.model.AssignedPerformancePlan;
import kz.uco.tsadv.modules.performance.model.AssignedPerformancePlanHistory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.inject.Inject;
import java.util.UUID;

@Component("tsadv_AssignedPerformancePlanChangedListener")
public class AssignedPerformancePlanChangedListener {

    @Inject
    protected TransactionalDataManager txDataManager;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void beforeCommit(EntityChangedEvent<AssignedPerformancePlan, UUID> event) {
        Id<AssignedPerformancePlan, UUID> entityId = event.getEntityId();
        AssignedPerformancePlan assignedPerformancePlan;
        if (event.getType().equals(EntityChangedEvent.Type.UPDATED)) {
            assignedPerformancePlan = txDataManager.load(entityId).view("assignedPerformancePlan.browse").one();
            if (event.getChanges().isChanged("stage") || event.getChanges().isChanged("status")) {
                createAssignedHistory(assignedPerformancePlan);
            }
        } else if (event.getType().equals(EntityChangedEvent.Type.CREATED)) {
            assignedPerformancePlan = txDataManager.load(entityId).view("assignedPerformancePlan.browse").one();
            createAssignedHistory(assignedPerformancePlan);
        }
    }

    protected void createAssignedHistory(AssignedPerformancePlan assignedPerformancePlan) {
        AssignedPerformancePlanHistory history = txDataManager.create(AssignedPerformancePlanHistory.class);
        history.setAssignedPerformancePlan(assignedPerformancePlan);
        history.setStage(assignedPerformancePlan.getStage());
        history.setStatus(assignedPerformancePlan.getStatus());
        txDataManager.save(history);
    }
}