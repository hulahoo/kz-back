package kz.uco.tsadv.listeners;

import com.haulmont.cuba.core.TransactionalDataManager;
import com.haulmont.cuba.core.app.events.EntityChangedEvent;
import kz.uco.tsadv.modules.personal.group.AssignmentGroupExt;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.inject.Inject;
import java.util.UUID;

@Component("tsadv_AssignmentExtChangedListener")
public class AssignmentExtChangedListener {

    @Inject
    protected TransactionalDataManager transactionalDataManager;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void beforeCommit(EntityChangedEvent<AssignmentExt, UUID> event) {
        if (event.getType().equals(EntityChangedEvent.Type.CREATED)
        || event.getType().equals(EntityChangedEvent.Type.UPDATED)) {
            AssignmentExt assignmentExt = transactionalDataManager.load(event.getEntityId()).view("assignment.full").one();
            AssignmentGroupExt assignmentGroupExt = assignmentExt.getGroup();
            if (assignmentGroupExt != null) {
                assignmentGroupExt = transactionalDataManager.load(AssignmentGroupExt.class).id(assignmentExt.getGroup().getId())
                        .view("assignmentGroup.view").optional().orElse(null);
                repeatCheck(assignmentExt, assignmentGroupExt);
                transactionalDataManager.save(assignmentGroupExt);
            }
        }
    }

    protected void repeatCheck(AssignmentExt assignmentExt, AssignmentGroupExt assignmentGroupExt) {
        if (assignmentGroupExt.getPersonGroup() == null || (assignmentGroupExt.getPersonGroup() != null
                && !assignmentGroupExt.getPersonGroup().equals(assignmentExt.getPersonGroup()))) {
            assignmentGroupExt.setPersonGroup(assignmentExt.getPersonGroup());
        }
        if (assignmentGroupExt.getOrganizationGroup() == null || (assignmentGroupExt.getOrganizationGroup() != null
                && !assignmentGroupExt.getOrganizationGroup().equals(assignmentExt.getOrganizationGroup()))) {
            assignmentGroupExt.setOrganizationGroup(assignmentExt.getOrganizationGroup());
        }
        if (assignmentGroupExt.getPositionGroup() == null || (assignmentGroupExt.getPositionGroup() != null
                && !assignmentGroupExt.getPositionGroup().equals(assignmentExt.getPositionGroup()))) {
            assignmentGroupExt.setPositionGroup(assignmentExt.getPositionGroup());
        }
        if (assignmentGroupExt.getJobGroup() == null || (assignmentGroupExt.getJobGroup() != null
                && !assignmentGroupExt.getJobGroup().equals(assignmentExt.getJobGroup()))) {
            assignmentGroupExt.setJobGroup(assignmentExt.getJobGroup());
        }
        if (assignmentGroupExt.getGradeGroup() == null || (assignmentGroupExt.getGradeGroup() != null
                && assignmentGroupExt.getGradeGroup().equals(assignmentExt.getGradeGroup()))) {
            assignmentGroupExt.setGradeGroup(assignmentExt.getGradeGroup());
        }
    }
}