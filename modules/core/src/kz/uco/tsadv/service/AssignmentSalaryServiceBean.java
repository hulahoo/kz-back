package kz.uco.tsadv.service;

import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Query;
import com.haulmont.cuba.core.global.Metadata;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.SalaryRequest;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;

@Service(AssignmentSalaryService.NAME)
public class AssignmentSalaryServiceBean implements AssignmentSalaryService {

    @Inject
    private Persistence persistence;

    @Inject
    protected CommonService commonService;

    @Inject
    private Metadata metadata;

    @Override
    public boolean isLastAssignment(UUID personGroupId, Date endDate) {
        return persistence.callInTransaction(em -> {
            Query query = em.createQuery("select count(e) from base$AssignmentExt e " +
                    " where e.personGroup.id = :personGroupId and e.startDate > :endDate " +
                    "   and e.assignmentStatus.code <> 'TERMINATED' ");
            query.setParameter("personGroupId", personGroupId);
            query.setParameter("endDate", endDate);
            return (Long) query.getSingleResult() == 0;
        });
    }

    @Override
    public boolean isLastSalary(UUID assignmentGroupId, Date endDate) {
        return persistence.callInTransaction(em -> {
            Query query = em.createQuery("select count(e) from tsadv$Salary e where e.assignmentGroup.id = :assignmentGroupId and e.startDate > :endDate");
            query.setParameter("assignmentGroupId", assignmentGroupId);
            query.setParameter("endDate", endDate);
            return (Long) query.getSingleResult() == 0;
        });
    }

    @Override
    public boolean isHaveRequest(List<String> types, Map<Integer, Object> params) {
        boolean isFirstAdded = false;
        StringBuilder builder = new StringBuilder();
        builder.append("select (");
        for (String s : types) {
            if (isFirstAdded) {
                builder.append("+");
            }
            builder.append(getQuery(s));
            isFirstAdded = true;
        }
        builder.append(")");
        return commonService.getCount(builder.toString(), params) != 0;
    }

    @Override
    public PersonGroupExt getPersonGroupByRequestId(UUID requestId, String view, Class entityClass) {
        return persistence.callInTransaction(em -> {
            MetaClass metaClass = metadata.getClass(entityClass);
            String queryStr = " select distinct e.personGroup from "
                    + metaClass.getName()
                    + " e where e.id = :id ";
            if (SalaryRequest.class.equals(entityClass)) {
                queryStr = "select distinct s.personGroup from tsadv$SalaryRequest e, base$AssignmentExt s " +
                        " where e.id = :id and s.group.id = e.assignmentGroup.id " +
                        " and current_date between s.startDate and e.endDate " +
                        "   and s.primaryFlag = 'TRUE' ";
            }
            Query query = em.createQuery(queryStr, PersonGroupExt.class).setParameter("id", requestId).setView(PersonGroupExt.class, view);
            List list = query.getResultList();
            return (PersonGroupExt) (list.isEmpty() ? null : list.get(0));
        });
    }

    private String getQuery(String s) {
        switch (s) {
            case "tsadv_assignment_request": {
                return "(select count(t.id)  from tsadv_assignment_request t where t.delete_ts is null and t.person_group_id = ?1 and t.status_id = ?2 )";
            }
            case "tsadv_assignment_salary_request": {
                return "(select count(t.id)  from tsadv_assignment_salary_request t where t.delete_ts is null and t.person_group_id = ?1 and t.status_id = ?2 )";
            }
            case "tsadv_salary_request": {
                return "(select count(t.id) from tsadv_salary_request t " +
                        "join base_assignment ss on ss.group_id = t.assignment_group_id and ?3 between ss.start_date and ss.end_date and ss.primary_flag is true and ss.delete_ts is null " +
                        "where ss.person_group_id = ?1 and t.delete_ts is null and t.status_id = ?2 )";
            }
            case "tsadv_temporary_translation_request": {
                return "(select count(t.id)  from tsadv_temporary_translation_request t where t.delete_ts is null and t.person_group_id = ?1 and t.status_id = ?2 )";
            }
        }
        return null;
    }

    @Override
    public double calculatePercentage(Double oldValue, Double newValue) {
        double percentageChange = (newValue * 100.0f) / oldValue;
        percentageChange = percentageChange - 100;
        return percentageChange;
    }
}
