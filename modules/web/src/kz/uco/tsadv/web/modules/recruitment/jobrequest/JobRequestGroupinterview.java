package kz.uco.tsadv.web.modules.recruitment.jobrequest;

import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.tsadv.modules.recruitment.model.JobRequest;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

public class JobRequestGroupinterview extends AbstractLookup {

    @Inject
    private GroupDatasource<JobRequest, UUID> jobRequestsDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        if (params.get("alreadyExistInterview") != null) {
            jobRequestsDs.setQuery("select e from tsadv$JobRequest e " +
                    "where e.interviews not in :param$alreadyExistInterview " +
                    "and e.candidatePersonGroup is not null " +
                    "and e.requisition.id = :param$requisition");
        }
    }
}