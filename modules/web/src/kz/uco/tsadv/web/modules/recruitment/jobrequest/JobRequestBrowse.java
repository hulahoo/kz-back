package kz.uco.tsadv.web.modules.recruitment.jobrequest;

import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.tsadv.modules.recruitment.model.JobRequest;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

public class JobRequestBrowse extends AbstractLookup {
    @Inject
    private GroupDatasource<JobRequest, UUID> jobRequestsDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        if (params.get("hrManagerPersonGroupId") != null) {
            jobRequestsDs.setQuery("select distinct e from tsadv$JobRequest e " +
                    "where (e.requisition.id in (select rm.requisition.id " +
                    "                             from tsadv$RequisitionMember rm " +
                    "                            where rm.personGroupId.id = :param$hrManagerPersonGroupId)" +
                    "   or e.requisition.recruiterPersonGroup.id = :param$hrManagerPersonGroupId)" +
                    "  and e.requestStatus = 6");
        }
    }
}