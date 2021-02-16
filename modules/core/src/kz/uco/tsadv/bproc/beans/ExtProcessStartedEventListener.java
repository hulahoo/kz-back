package kz.uco.tsadv.bproc.beans;

import com.haulmont.addon.bproc.engine.eventlistener.ProcessStartedEventListener;
import com.haulmont.addon.bproc.entity.ProcessDefinitionData;
import com.haulmont.addon.bproc.entity.ProcessInstanceData;
import com.haulmont.addon.bproc.events.ProcessStartedEvent;
import kz.uco.tsadv.bproc.events.ExtProcessStartedEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.common.engine.impl.cfg.TransactionState;
import org.flowable.engine.delegate.event.FlowableEntityWithVariablesEvent;
import org.flowable.engine.delegate.event.FlowableProcessEngineEvent;

import java.util.Map;

/**
 * @author Alibek Berdaulet
 */
public class ExtProcessStartedEventListener extends ProcessStartedEventListener {

    @Override
    public void onEvent(FlowableEvent event) {
        if (!(event instanceof FlowableProcessEngineEvent)) return;
        @SuppressWarnings("unchecked") Map<String, Object> variables = ((FlowableEntityWithVariablesEvent) event).getVariables();
        String processDefinitionId = ((FlowableProcessEngineEvent) event).getProcessDefinitionId();
        String processInstanceId = ((FlowableProcessEngineEvent) event).getProcessInstanceId();
        ProcessDefinitionData processDefinitionData = bprocRepositoryService.getProcessDefinitionById(processDefinitionId);
        ProcessInstanceData processInstanceData = metadata.create(ProcessInstanceData.class);
        processInstanceData.setId(processInstanceId);
        processInstanceData.setProcessDefinitionId(processDefinitionData.getId());
        processInstanceData.setProcessDefinitionKey(processDefinitionData.getKey());
        processInstanceData.setProcessDefinitionName(processDefinitionData.getName());
        processInstanceData.setProcessDefinitionVersion(processDefinitionData.getVersion());
        ProcessStartedEvent processStartedEvent = new ExtProcessStartedEvent(this)
                .withVariables(variables)
                .withProcessDefinitionData(processDefinitionData)
                .withProcessInstanceData(processInstanceData);
        events.publish(processStartedEvent);
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
