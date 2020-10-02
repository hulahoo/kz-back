package kz.uco.tsadv.service;

import com.haulmont.cuba.core.global.View;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.personal.model.Job;
import kz.uco.tsadv.modules.personal.model.PositionExt;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(PositionService.NAME)
public class PositionServiceBean implements PositionService {

    @Inject
    private CommonService commonService;

    @Override
    public List<Job> getExistingJobsInactiveInNearFuture(PositionExt position) {
        List<Job> jobs = new ArrayList<>();
        if (position==null || position.getJobGroup()==null) {
            return jobs;
        }
        String queryString = "select e from tsadv$Job e " +
                "where e.endDate >= :positionStartDate and e.endDate < :positionEndDate and " +
                "e.group.id = :jobGroupId " +
                "order by e.endDate DESC";
        Map<String, Object> params = new HashMap<>();
        params.put("positionStartDate", position.getStartDate());
        params.put("positionEndDate", position.getEndDate());
        params.put("jobGroupId", position.getJobGroup().getId());
        jobs = commonService.getEntities(Job.class, queryString, params, View.LOCAL);
        return jobs;
    }
/*
    @Override
    public String generateNextRequestNumber() {
        return persistence.callInTransaction(em -> {
            Query query = em.createQuery(
                    "select MAX(e.requestNumber) from tsadv$PositionChangeRequest e " +
                            " where LENGTH(e.requestNumber) = ( select MAX(LENGTH(a.requestNumber)) from tsadv$PositionChangeRequest a )");
            int requestNum = 0;
            String lastNum = (String) query.getSingleResult();
            if (lastNum!= null){
                requestNum = Integer.parseInt(lastNum);
            }
            return String.valueOf(( requestNum + 1));
        });
    }

    @Override
    public boolean hasRequestNumber(String requestNumber) {
        return persistence.callInTransaction(em -> {
            Query query = em.createQuery(
                    "select count(e) from tsadv$PositionChangeRequest e " +
                            "where e.requestNumber = :requestNumber");
            query.setParameter("requestNumber", requestNumber);
            return (Long) query.getSingleResult() > 0;
        });
    }

    @Override
    public boolean hasRequestNumber(String requestNumber, UUID id) {
        return persistence.callInTransaction(em -> {
            Query query = em.createQuery(
                    "select count(e) from tsadv$PositionChangeRequest e " +
                            "where e.requestNumber = :requestNumber and e.id <> :id ");
            query.setParameter("requestNumber", requestNumber);
            query.setParameter("id", id);
            return (Long) query.getSingleResult() > 0;
        });
    }*/
}