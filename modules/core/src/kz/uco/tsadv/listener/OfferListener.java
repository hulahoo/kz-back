package kz.uco.tsadv.listener;

import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.PersistenceTools;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.listener.AfterInsertEntityListener;
import com.haulmont.cuba.core.listener.AfterUpdateEntityListener;
import kz.uco.tsadv.modules.recruitment.dictionary.DicInterviewReason;
import kz.uco.tsadv.modules.recruitment.dictionary.DicJobRequestReason;
import kz.uco.tsadv.modules.recruitment.enums.InterviewStatus;
import kz.uco.tsadv.modules.recruitment.enums.JobRequestStatus;
import kz.uco.tsadv.modules.recruitment.model.Interview;
import kz.uco.tsadv.modules.recruitment.model.JobRequest;
import kz.uco.tsadv.modules.recruitment.model.Offer;
import kz.uco.base.service.common.CommonService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.sql.Connection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component("tsadv_OfferListener")
public class OfferListener implements AfterInsertEntityListener<Offer>, AfterUpdateEntityListener<Offer> {

    @Inject
    private Persistence persistence;

    @Inject
    private DataManager dataManager;

    @Inject
    private CommonService commonService;

    @Override
    public void onAfterInsert(Offer entity, Connection connection) {

    }

    @Override
    public void onAfterUpdate(Offer entity, Connection connection) {
        PersistenceTools persistenceTools = persistence.getTools();
        if (persistenceTools.isDirty(entity, "status")) {
            Set<Entity> commitInstances = new HashSet<>();

            JobRequest jobRequest = dataManager.reload(entity.getJobRequest(), "jobRequest.full");

            switch (entity.getStatus()) {
                case APPROVED:
                    jobRequest.setRequestStatus(JobRequestStatus.MADE_OFFER);
                    commitInstances.add(jobRequest);
                    break;
                case ACCEPTED:
                    jobRequest.setRequestStatus(JobRequestStatus.HIRED);
                    commitInstances.add(jobRequest);

                    Map<String, Object> jrParams = new HashMap<>();
                    jrParams.put("jobRequestId", jobRequest.getId());
                    jrParams.put("candidatePersonGroupId", jobRequest.getCandidatePersonGroup().getId());

                    for (JobRequest jr : commonService.getEntities(JobRequest.class,
                            "select e " +
                                    "    from tsadv$JobRequest e " +
                                    "   where e.candidatePersonGroup.id = :candidatePersonGroupId" +
                                    "     and e.id <> :jobRequestId " +
                                    "     and e.requestStatus in (1, 3 ,4) " /*ON_APPROVAL(1), INTERVIEW(3), MADE_OFFER(4)*/
                            , jrParams
                            , "jobRequest.full")) {
                        jr.setRequestStatus(JobRequestStatus.REJECTED);
                        jr.setJobRequestReason(commonService.getEntity(DicJobRequestReason.class, "HIRED_FOR_OTHER_REQUISITION"));
                        jr.setReason(jobRequest.getRequisition().getCode());
                        commitInstances.add(jr);

                        Map<String, Object> iParams = new HashMap<>();
                        iParams.put("jrId", jr.getId());
                        for (Interview i : commonService.getEntities(Interview.class,
                                "select e " +
                                        "    from tsadv$Interview e " +
                                        "   where e.jobRequest.id = :jrId" +
                                        "     and e.interviewStatus in (10, 20, 30) " /*DRAFT(10), ON_APPROVAL(20), PLANNED(30)*/
                                , iParams
                                , "interview.full")) {
                            i.setInterviewStatus(InterviewStatus.CANCELLED);
                            i.setInterviewReason(commonService.getEntity(DicInterviewReason.class, "HIRED_FOR_OTHER_REQUISITION"));
                            i.setReason(jobRequest.getRequisition().getCode());
                            commitInstances.add(i);
                        }
                    }
                    break;

                case REJECTED:
                    jobRequest.setRequestStatus(JobRequestStatus.REJECTED);
                    commitInstances.add(jobRequest);
                    break;
            }
            if (!commitInstances.isEmpty())
                dataManager.commit(new CommitContext(commitInstances));
        }

    }


}