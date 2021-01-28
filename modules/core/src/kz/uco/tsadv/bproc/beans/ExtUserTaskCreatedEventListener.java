package kz.uco.tsadv.bproc.beans;

import com.haulmont.addon.bproc.engine.eventlistener.UserTaskCreatedEventListener;
import com.haulmont.addon.bproc.entity.ProcessDefinitionData;
import com.haulmont.addon.bproc.entity.TaskData;
import com.haulmont.cuba.security.entity.User;
import kz.uco.tsadv.bproc.events.ExtUserTaskCreatedEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEntityEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.common.engine.impl.cfg.TransactionState;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;

import java.util.Optional;

/**
 * @author Alibek Berdaulet
 */
public class ExtUserTaskCreatedEventListener extends UserTaskCreatedEventListener {

    @Override
    public void onEvent(FlowableEvent event) {
        FlowableEntityEvent entityEvent = (FlowableEntityEvent) event;
        TaskEntity taskEntity = (TaskEntity) entityEvent.getEntity();
        Optional<User> userOpt = findAssigneeUserOpt(taskEntity.getAssignee());
        TaskData taskData = engineEntitiesConverter.createTaskData(taskEntity);
        ProcessDefinitionData processDefinitionData = bprocRepositoryService.getProcessDefinitionById(taskEntity.getProcessDefinitionId());
        events.publish(new ExtUserTaskCreatedEvent(this, taskData, userOpt.orElse(null), processDefinitionData, userOpt.isPresent() ? null : taskEntity.getCandidates()));
    }

    @Override
    public boolean isFailOnException() {
        return true;
    }

    @Override
    public boolean isFireOnTransactionLifecycleEvent() {
        return true;
    }

    @Override
    public String getOnTransaction() {
        return TransactionState.COMMITTING.name();
    }
}
