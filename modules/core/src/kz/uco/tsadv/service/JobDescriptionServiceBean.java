package kz.uco.tsadv.service;

import com.haulmont.addon.bproc.entity.HistoricVariableInstanceData;
import com.haulmont.addon.bproc.entity.ProcessInstanceData;
import com.haulmont.addon.bproc.service.BprocHistoricService;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.entity.models.JobDescriptionRequestJson;
import kz.uco.tsadv.modules.administration.TsadvUser;
import kz.uco.tsadv.modules.hr.JobDescriptionRequest;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service(JobDescriptionService.NAME)
public class JobDescriptionServiceBean implements JobDescriptionService {

    @Inject
    protected CommonService commonService;
    @Inject
    protected Metadata metadata;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected BprocService bprocService;
    @Inject
    protected BprocHistoricService bprocHistoricService;
    protected SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");

    @Override
    public List<JobDescriptionRequestJson> getJobDescriptionRequests(String positionId) {
        List<JobDescriptionRequestJson> result = new ArrayList<>();
        if (positionId.isEmpty()) {
            return result;
        }
        List<JobDescriptionRequest> jobDescriptionRequests = dataManager.load(JobDescriptionRequest.class)
                .query("select e from  tsadv_JobDescriptionRequest e " +
                        " where e.positionGroup.id = :positionGroupId order by e.requestDate ")
                .parameter("positionGroupId", UUID.fromString(positionId))
                .view("jobDescriptionRequest-edit").list();
        for (JobDescriptionRequest jobDescriptionRequest : jobDescriptionRequests) {
            JobDescriptionRequestJson jobDescriptionRequestJson = metadata.create(JobDescriptionRequestJson.class);
            jobDescriptionRequestJson.setRequestDate(dateFormat.format(jobDescriptionRequest.getRequestDate()));
            jobDescriptionRequestJson.setRequestNumber(jobDescriptionRequest.getRequestNumber().toString());
            jobDescriptionRequestJson.setRequestStatus(jobDescriptionRequest.getStatus().getLangValue());
            jobDescriptionRequestJson.setStatusCode(jobDescriptionRequest.getStatus().getCode());
            jobDescriptionRequestJson.setId(jobDescriptionRequest.getId());
            ProcessInstanceData processInstanceData = bprocService.getProcessInstanceData(jobDescriptionRequest.getProcessInstanceBusinessKey(),
                    jobDescriptionRequest.getProcessDefinitionKey());
            List<HistoricVariableInstanceData> initiatorVariableList = bprocHistoricService
                    .createHistoricVariableInstanceDataQuery().processInstanceId(processInstanceData.getId())
                    .variableName("initiator")
                    .list();
            if (!initiatorVariableList.isEmpty()) {
                TsadvUser initiator = dataManager.reload((TsadvUser) initiatorVariableList.get(0).getValue(), "user-fioWithLogin");
                jobDescriptionRequestJson.setInitiator(initiator.getPersonGroup().getFullName());
            } else {
                jobDescriptionRequestJson.setInitiator("");
            }
            result.add(jobDescriptionRequestJson);
        }
        return result;
    }
}