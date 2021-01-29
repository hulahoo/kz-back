package kz.uco.tsadv.bproc.beans;

import com.haulmont.addon.bproc.data.Outcome;
import com.haulmont.addon.bproc.data.OutcomesContainer;
import com.haulmont.addon.bproc.entity.TaskData;
import com.haulmont.addon.bproc.service.BprocTaskServiceBean;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.TimeSource;
import kz.uco.tsadv.entity.bproc.ExtTaskData;
import org.flowable.engine.ProcessEngines;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;

import java.util.Map;

/**
 * @author Alibek Berdaulet
 */
public class ExtBprocTaskServiceBean extends BprocTaskServiceBean {

    @Override
    public void completeWithOutcome(TaskData taskData, String outcomeId, Map<String, Object> processVariables) {
        Outcome outcomeInstance = new Outcome();
        outcomeInstance.setTaskDefinitionKey(taskData.getTaskDefinitionKey());
        outcomeInstance.setDate(AppBeans.get(TimeSource.class).currentTimestamp());
        outcomeInstance.setExecutionId(taskData.getExecutionId());
        outcomeInstance.setOutcomeId(outcomeId);
        outcomeInstance.setUser(userSessionSource.getUserSession().getCurrentOrSubstitutedUser().getId().toString());

        String comment = (String) processVariables.get("comment");
        if (comment != null) processVariables.put(ExtTaskData.getUniqueCommentKey(outcomeInstance), comment);

        RuntimeService runtimeService = ProcessEngines.getDefaultProcessEngine().getRuntimeService();
        TaskService taskService = ProcessEngines.getDefaultProcessEngine().getTaskService();
        String outcomesContainerVariableName = taskData.getTaskDefinitionKey() + "_result";
        synchronized (this) {
            OutcomesContainer outcomesContainer = (OutcomesContainer) runtimeService.getVariable(taskData.getExecutionId(), outcomesContainerVariableName);
            if (outcomesContainer == null) outcomesContainer = new OutcomesContainer();

            //if nrCofCompletedInstances == 0 then we should reset the outcomesContainer, because we might return to this userTask for the second
            outcomesContainer.getOutcomes().add(outcomeInstance);
            processVariables.put(outcomesContainerVariableName, outcomesContainer);
            taskService.complete(taskData.getId(), processVariables);
        }
    }

}
