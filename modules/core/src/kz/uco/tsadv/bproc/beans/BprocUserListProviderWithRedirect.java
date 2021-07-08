package kz.uco.tsadv.bproc.beans;

import com.haulmont.addon.bproc.entity.ExecutionData;
import com.haulmont.cuba.core.TransactionalDataManager;
import com.haulmont.cuba.core.entity.contracts.Id;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.security.entity.User;
import kz.uco.tsadv.entity.bproc.AbstractBprocRequest;
import kz.uco.tsadv.exceptions.PortalException;
import kz.uco.tsadv.modules.administration.TsadvUser;
import kz.uco.tsadv.modules.bpm.BprocReassignment;
import kz.uco.tsadv.service.BpmUserSubstitutionService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.*;

/**
 * @author Alibek Berdaulet
 */
@Component(BprocUserListProviderWithRedirect.NAME)
public class BprocUserListProviderWithRedirect extends BprocUserListProvider {

    public static final String NAME = "tsadv_BprocUserListProviderWithRedirect";

    @Inject
    protected BpmUserSubstitutionService bpmUserSubstitutionService;

    @Inject
    protected Metadata metadata;

    @Inject
    protected TimeSource timeSource;

    @Inject
    protected TransactionalDataManager transactionalDataManager;

    @Override
    public List<User> get(String executionId) {
        List<User> users = super.get(executionId);

        if (users.size() == 1) {
            return redirect((TsadvUser) users.get(0), executionId);
        }

        return users;
    }

    protected List<User> redirect(TsadvUser user, String executionId) {
        String idBpmUserSubstitutionPath = bpmUserSubstitutionService.getCurrentBpmUserSubstitution(user, true);
        if (idBpmUserSubstitutionPath == null) return Collections.singletonList(user);

        ExecutionData executionData = bprocRuntimeService.createExecutionDataQuery().executionId(executionId).singleResult();
        List<BprocReassignment> reassignments = bprocService.getBprocReassignments(executionId);

        int order = reassignments.stream().map(BprocReassignment::getOrder).max(Integer::compareTo).orElse(0);

        Set<String> usedIdSet = new HashSet<>();
        String[] idBpmUserSubstitution = idBpmUserSubstitutionPath.split("\\*");

        for (int i = 0; i < idBpmUserSubstitution.length - 1; ) {
            if (usedIdSet.contains(idBpmUserSubstitution[i]))
                throw new PortalException("Impossible define user substitution!");

            usedIdSet.add(idBpmUserSubstitution[i]);

            BprocReassignment bprocReassignment = metadata.create(BprocReassignment.class);
            bprocReassignment.setOrder(++order);
            bprocReassignment.setAssignee(user);
            bprocReassignment.setStartTime(timeSource.currentTimestamp());
            bprocReassignment.setEndTime(timeSource.currentTimestamp());
            bprocReassignment.setExecutionId(executionId);
            bprocReassignment.setOutcome(AbstractBprocRequest.OUTCOME_AUTO_REDIRECT);
            bprocReassignment.setProcessInstanceId(executionData.getProcessInstanceId());

            transactionalDataManager.save(bprocReassignment);

            user = transactionalDataManager.load(Id.of(UUID.fromString(idBpmUserSubstitution[++i]), TsadvUser.class))
                    .view(View.MINIMAL)
                    .one();
        }

        return Collections.singletonList(user);
    }
}
