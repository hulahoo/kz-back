package kz.uco.tsadv.bproc.beans;

import com.haulmont.addon.bproc.entity.ExecutionData;
import com.haulmont.addon.bproc.provider.UserListProvider;
import com.haulmont.addon.bproc.service.BprocRuntimeService;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.security.entity.User;
import kz.uco.tsadv.entity.bproc.AbstractBprocRequest;
import kz.uco.tsadv.service.BprocService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;

@Component(BprocUserListProvider.NAME)
public class BprocUserListProvider implements UserListProvider {
    public static final String NAME = "tsadv_BprocUserListProvider";
    @Inject
    protected DataManager dataManager;

    @Inject
    private BprocRuntimeService bprocRuntimeService;

    @Inject
    protected Metadata metadata;

    @Inject
    protected BprocService bprocService;

    @Override
    @SuppressWarnings("unchecked")
    public List<User> get(String executionId) {
        ExecutionData executionData = bprocRuntimeService.createExecutionDataQuery().executionId(executionId).singleResult();
        AbstractBprocRequest bprocRequest = (AbstractBprocRequest) bprocRuntimeService.getVariable(executionId, "entity");
        return (List<User>) bprocService.getActors(bprocRequest.getId(), executionData.getActivityId(), View.MINIMAL);
    }

}