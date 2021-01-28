package kz.uco.tsadv.bproc.events;

import com.haulmont.addon.bproc.entity.ProcessDefinitionData;
import com.haulmont.addon.bproc.entity.TaskData;
import com.haulmont.addon.bproc.events.UserTaskCreatedEvent;
import com.haulmont.cuba.security.entity.User;
import org.flowable.identitylink.api.IdentityLink;

import java.util.Set;

/**
 * @author Alibek Berdaulet
 */
public class ExtUserTaskCreatedEvent extends UserTaskCreatedEvent {

    Set<IdentityLink> candidates;

    public ExtUserTaskCreatedEvent(Object source, TaskData taskData, User user, ProcessDefinitionData processDefinitionData, Set<IdentityLink> candidates) {
        super(source, taskData, user, processDefinitionData);
        this.candidates = candidates;
    }

    public Set<IdentityLink> getCandidates() {
        return candidates;
    }

}
