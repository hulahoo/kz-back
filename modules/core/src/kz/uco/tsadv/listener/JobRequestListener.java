package kz.uco.tsadv.listener;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.PersistenceTools;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.listener.AfterInsertEntityListener;
import com.haulmont.cuba.core.listener.AfterUpdateEntityListener;
import kz.uco.tsadv.modules.recruitment.enums.JobRequestStatus;
import kz.uco.tsadv.modules.recruitment.model.JobRequest;
import kz.uco.tsadv.modules.recruitment.model.JobRequestHistory;
import kz.uco.base.service.NotificationService;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.service.EmployeeService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

@Component("tsadv_JobRequestListener")
public class JobRequestListener implements AfterInsertEntityListener<JobRequest>, AfterUpdateEntityListener<JobRequest> {


    @Inject
    protected Persistence persistence;
    @Inject
    protected Metadata metadata;
    @Inject
    protected CommonService commonService;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected NotificationService notificationService;
    @Inject
    protected EmployeeService employeeService;


    //todo call on onBeforeInsertUpdate or use queryRunner
    @Override
    public void onAfterInsert(JobRequest entity, Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) connection.commit(); //todo

            try (Transaction tx = persistence.createTransaction()) {
                EntityManager em = persistence.getEntityManager();
                em.persist(createJobRequestHistory(entity));
                tx.commit();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    //todo call on onBeforeUpdate or use queryRunner
    @Override
    public void onAfterUpdate(JobRequest entity, Connection connection) {
        PersistenceTools persistenceTools = persistence.getTools();
        if (persistenceTools.isDirty(entity, "requestStatus")) {
            try (Transaction tx = persistence.createTransaction()) {
                EntityManager em = persistence.getEntityManager();
                em.persist(createJobRequestHistory(entity));
                if (entity.getRequestStatus().equals(JobRequestStatus.HIRED)) {
                    List<JobRequest> jobRequestList = getChangedJobRequestList(entity);
                    jobRequestList.forEach(jobRequest -> em.merge(jobRequest));
                }
                tx.commit();
            }
        }
    }

    protected JobRequestHistory createJobRequestHistory(JobRequest jobRequest) {
        JobRequestHistory interviewHistory = metadata.create(JobRequestHistory.class);

        interviewHistory.setJobRequest(jobRequest);
        interviewHistory.setJobRequestStatus(jobRequest.getRequestStatus());
        return interviewHistory;
    }


    protected List<JobRequest> getChangedJobRequestList(JobRequest entity) {
        List<JobRequest> jobRequestList;
        Map<String, Object> map = new HashMap<>();
        map.put("candidatePersonGroupId", entity.getCandidatePersonGroup() != null ? entity.getCandidatePersonGroup().getId() : null);
        map.put("jobRequestId", entity.getId());
        String query = "select e from tsadv$JobRequest e where e.candidatePersonGroup.id = :candidatePersonGroupId" +
                "       and e.requestStatus <> 5" +
                "       and e.id <> :jobRequestId";
        jobRequestList = commonService.getEntities(JobRequest.class, query,
                map, "jobRequest.listener.view");

        jobRequestList.forEach(jobRequest -> {
            jobRequest.setRequestStatus(JobRequestStatus.REJECTED);
            jobRequest.setReason("Нанят по другой должности");
        });
        return jobRequestList;
    }








}