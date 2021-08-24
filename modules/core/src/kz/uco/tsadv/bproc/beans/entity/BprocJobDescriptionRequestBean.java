package kz.uco.tsadv.bproc.beans.entity;

import com.haulmont.addon.bproc.entity.ProcessInstanceData;
import com.haulmont.cuba.core.entity.contracts.Id;
import com.haulmont.cuba.core.global.Metadata;
import kz.uco.tsadv.entity.bproc.ExtTaskData;
import kz.uco.tsadv.modules.hr.JobDescription;
import kz.uco.tsadv.modules.hr.JobDescriptionRequest;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;

@Component(BprocJobDescriptionRequestBean.NAME)
public class BprocJobDescriptionRequestBean<T extends JobDescriptionRequest> extends AbstractBprocEntityBean<T> {
    public static final String NAME = "tsadv_BprocJobDescriptionRequestBean";
    @Inject
    protected Metadata metadata;


    @Override
    public String changeNotificationTemplateCode(String notificationTemplateCode, T entity) {
        if (notificationTemplateCode.equals("bpm.jobDescriptionRequest.toapprove.notification")) {
            ProcessInstanceData processInstanceData = bprocService.getProcessInstanceData(entity.getProcessInstanceBusinessKey(), entity.getProcessDefinitionKey());
            List<ExtTaskData> processTasks = bprocService.getProcessTasks(processInstanceData);
            ExtTaskData extTaskData = processTasks.stream()
                    .filter(taskData -> taskData.getEndTime() == null)
                    .findAny()
                    .orElse(null);
            if ("c_and_b_task2".equalsIgnoreCase(extTaskData.getTaskDefinitionKey())) {
                notificationTemplateCode = "bpm.jobDescriptionRequest.signing.notification";
            }
        }
        return super.changeNotificationTemplateCode(notificationTemplateCode, entity);
    }

    @Override
    public void approve(T entity) {
        super.approve(entity);
        JobDescriptionRequest jobDescriptionRequest = transactionalDataManager.load(Id.of(entity.getId(), JobDescriptionRequest.class))
                .view("jobDescriptionRequest-edit").optional().orElse(null);
        if (jobDescriptionRequest == null) {
            return;
        }
        List<JobDescription> jobDescriptionRequests = transactionalDataManager.load(JobDescription.class)
                .query("select e from tsadv_JobDescription e " +
                        " join tsadv_JobDescriptionRequest jd on jd.positionGroup.id = e.positionGroup.id " +
                        " where jd.id = :jobDescriptionRequestId ")
                .parameter("jobDescriptionRequestId", entity.getId()).view("jobDescription-for-position-edit")
                .list();
        JobDescription jobDescription = jobDescriptionRequests.stream().findFirst().orElse(null);
        if (jobDescription == null) {
            jobDescription = metadata.create(JobDescription.class);
        }
        jobDescription.setPositionGroup(jobDescriptionRequest.getPositionGroup());
        jobDescription.setPositionDuties(jobDescriptionRequest.getPositionDuties());
        jobDescription.setCompulsoryQualificationRequirements(jobDescriptionRequest.getCompulsoryQualificationRequirements());
        jobDescription.setBasicInteractionsAtWork(jobDescriptionRequest.getBasicInteractionsAtWork());
        jobDescription.setGeneralAdditionalRequirements(jobDescriptionRequest.getGeneralAdditionalRequirements());
        jobDescription.setFile(jobDescriptionRequest.getFile());
        jobDescription.setRequest(jobDescriptionRequest);
        for (JobDescription jobDescription1 : jobDescriptionRequests) {
            if (!jobDescription.getId().equals(jobDescription1.getId())) {
                transactionalDataManager.remove(jobDescription1);
            }
        }
        transactionalDataManager.save(jobDescription);
    }
}