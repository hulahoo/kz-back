package kz.uco.tsadv.listener;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.listener.*;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.IntegrationException;
import kz.uco.tsadv.config.IntegrationConfig;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.dictionary.DicAssignmentStatus;
import kz.uco.tsadv.modules.personal.dictionary.DicPersonType;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.personal.model.Dismissal;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.service.EmployeeService;
import kz.uco.tsadv.service.IntegrationService;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.sql.Connection;
import java.util.*;

@Component("tsadv_DismissalListener")
public class DismissalListener implements BeforeInsertEntityListener<Dismissal>,
        BeforeUpdateEntityListener<Dismissal>,
        AfterInsertEntityListener<Dismissal>,
        AfterDeleteEntityListener<Dismissal>,
        AfterUpdateEntityListener<Dismissal>,
        BeforeDeleteEntityListener<Dismissal> {

    @Inject
    protected EmployeeService employeeService;

    @Inject
    protected Persistence persistence;

    @Inject
    protected Metadata metadata;

    @Inject
    protected CommonService commonService;

    @Inject
    protected DataManager dataManager;

    @Inject
    protected IntegrationConfig integrationConfig;

    @Inject
    protected IntegrationService integrationService;


    @Override
    public void onBeforeInsert(Dismissal entity, EntityManager entityManager) {
        dismissEmployee(entity, entityManager, false);
    }

    @Override
    public void onBeforeUpdate(Dismissal entity, EntityManager entityManager) {
//        dismissEmployee(entity, entityManager, true);
    }

    @Override
    public void onAfterInsert(Dismissal entity, Connection connection) {
        if (!this.integrationConfig.getIsIntegrationOff()) {
            try {
                this.integrationService.createDismissal(entity, connection);
            } catch (Exception e) {
                if (e.getCause() != null) {
                    throw new IntegrationException(e.getCause().getMessage());
                } else {
                    throw new IntegrationException(e.getMessage());
                }
            }
        }
    }

    protected void dismissEmployee(Dismissal entity, EntityManager entityManager, boolean isUpdate) {
        if (isUpdate && !persistence.getTools().isDirty(entity, "dismissalDate")) return;

        Date dismissalDate = entity.getDismissalDate();
        Date oldDismissalDate = isUpdate
                ? (Date) persistence.getTools().getOldValue(entity, "dismissalDate")
                : null;

        Map<String, Object> params = new HashMap<>();
        params.put("personGroupId", entity.getPersonGroup().getId());
        params.put("dismissalDate", oldDismissalDate != null ? DateUtils.addDays(oldDismissalDate, 1) : dismissalDate);

        PersonExt person = getPerson(params);
        AssignmentExt assignment = getAssignment(params);

        person.setType(commonService.getEntity(DicPersonType.class, "EXEMPLOYEE"));

        person.setStartDate(DateUtils.addDays(dismissalDate, 1));
        person.setWriteHistory(!isUpdate);
        person.setUpdatedBy(person.getUpdatedBy() + "_integrationOff");
        entityManager.merge(person);

        if (assignment != null) {
            assignment.setStartDate(DateUtils.addDays(dismissalDate, 1));
            assignment.setWriteHistory(!isUpdate);
            assignment.setAssignmentStatus(commonService.getEntity(DicAssignmentStatus.class, "TERMINATED"));
            entityManager.merge(assignment);
        }

        if (isUpdate) {
            params.put("dismissalDate", dismissalDate);
            PersonExt oldPerson = getPerson(params);
            AssignmentExt oldAssignment = getAssignment(params);
            if (oldPerson != null) {
                oldPerson.setWriteHistory(false);
                oldPerson.setEndDate(dismissalDate);
                entityManager.merge(oldPerson);
            }
            if (oldAssignment != null) {
                oldAssignment.setWriteHistory(false);
                oldAssignment.setEndDate(dismissalDate);
                entityManager.merge(oldAssignment);
            }
        }
    }

    protected AssignmentExt getAssignment(Map<String, Object> params) {
        AssignmentExt entity = commonService.getEntity(
                AssignmentExt.class,
                "select e from base$AssignmentExt e where e.personGroup.id = :personGroupId " +
                        "and :dismissalDate between e.startDate and e.endDate " +
                        "and e.primaryFlag = true",
                params,
                "assignment.full");
        if (entity != null)
            entity.setUpdatedBy(Optional.ofNullable(entity.getUpdatedBy()).orElse("") + "_integrationOff");
        return entity;
    }

    protected PersonExt getPerson(Map<String, Object> params) {
        PersonExt entity = commonService.getEntity(PersonExt.class,
                "select e from base$PersonExt e " +
                        " where e.group.id = :personGroupId and :dismissalDate between e.startDate and e.endDate",
                params,
                "person.full");
        if (entity != null)
            entity.setUpdatedBy(Optional.ofNullable(entity.getUpdatedBy()).orElse("") + "_integrationOff");
        return entity;
    }

    @Override
    public void onAfterDelete(Dismissal entity, Connection connection) {
        if (!this.integrationConfig.getIsIntegrationOff()) {
            try {
                this.integrationService.deleteDismissal(entity, connection);
            } catch (Exception e) {
                if (e.getCause() != null) {
                    throw new IntegrationException(e.getCause().getMessage());
                } else {
                    throw new IntegrationException(e.getMessage());
                }
            }
        }
    }

    @Override
    public void onAfterUpdate(Dismissal entity, Connection connection) {
        if (!this.integrationConfig.getIsIntegrationOff()) {
            try {
                this.integrationService.updateDismissal(entity, connection);
            } catch (Exception e) {
                if (e.getCause() != null) {
                    throw new IntegrationException(e.getCause().getMessage());
                } else {
                    throw new IntegrationException(e.getMessage());
                }
            }
        }
    }

    @Override
    public void onBeforeDelete(Dismissal entity, EntityManager entityManager) {
        undismissEmployee(entity, entityManager);
    }

    protected void undismissEmployee(Dismissal entity, EntityManager entityManager) {
        Date dismissalDate = entity.getDismissalDate();

        //удаляет из базы сущности Employee со статусом "EXEMPLOYEE', созданные при увольнении.
        removeExEmployees(entity.getPersonGroup().getId(), dismissalDate, entityManager);

        //меняет дату завершения для сущности Employee, установленную при увольнении.
        changeEmployeeEndDate(entity.getPersonGroup().getId(), dismissalDate, entityManager);

        //меняет дату начала и статус сущности Assignment, созданной при увольнении,
        //удаляет старую запись Assignment
        changeAssignments(entity.getPersonGroup().getId(), dismissalDate, entityManager);
    }

    protected void removeExEmployees(UUID personGroupId, Date dismissalDate, EntityManager entityManager) {
        List<PersonExt> personExEmployees = getExemployeePersonList(personGroupId, dismissalDate);
        personExEmployees.forEach(entityManager::remove);
    }

    protected List<PersonExt> getExemployeePersonList(UUID personGroupId, Date dismissalDate) {
        String queryString = "SELECT e FROM base$PersonExt e,\n" +
                "   tsadv$DicPersonType pt\n" +
                "   WHERE e.group.id = :personGroupId\n" +
                "   AND e.startDate = :dismissalDatePlusOne\n" +
                "   AND pt.id = e.type.id\n" +
                "   AND pt.code = 'EXEMPLOYEE'";
        Map<String, Object> params = new HashMap<>();
        params.put("personGroupId", personGroupId);
        params.put("dismissalDatePlusOne", DateUtils.addDays(dismissalDate, 1));
        return commonService.getEntities(
                PersonExt.class,
                queryString,
                params,
                "person.full");
    }

    protected void changeEmployeeEndDate(UUID personGroupId, Date dismissalDate, EntityManager entityManager) {
        PersonExt personEmployee = getPersonToChange(personGroupId, dismissalDate);
        if (personEmployee != null) {
            personEmployee.setEndDate(CommonUtils.getEndOfTime());
            entityManager.merge(personEmployee);
        }
    }

    protected PersonExt getPersonToChange(UUID personGroupId, Date dismissalDate) {
        String queryString = "SELECT e FROM base$PersonExt e,\n" +
                "   tsadv$DicPersonType pt\n" +
                "   WHERE e.group.id = :personGroupId\n" +
                "   AND e.endDate = :dismissalDate\n" +
                "   AND e.deleteTs IS NULL\n" +
                "   AND pt.id = e.type.id";
        Map<String, Object> params = new HashMap<>();
        params.put("personGroupId", personGroupId);
        params.put("dismissalDate", dismissalDate);
        return commonService.emQueryFirstResult(
                PersonExt.class,
                queryString,
                params,
                "person.full");
    }

    protected void changeAssignments(UUID personGroupId, Date dismissalDate, EntityManager entityManager) {
        AssignmentExt newAssignment = getNewAssignment(personGroupId, dismissalDate);
        AssignmentExt oldAssignment = getOldAssignment(personGroupId, dismissalDate);
        if (newAssignment != null && oldAssignment != null) {
            newAssignment.setAssignmentStatus(oldAssignment.getAssignmentStatus());
            newAssignment.setStartDate(oldAssignment.getStartDate());
            entityManager.remove(oldAssignment);
            entityManager.merge(newAssignment);
        }
    }

    protected AssignmentExt getNewAssignment(UUID personGroupId, Date dismissalDate) {
        String queryString = "SELECT e from base$AssignmentExt e\n" +
                "   WHERE e.personGroup.id = :personGroupId\n" +
                "   AND e.startDate = :dismissalDatePlusOne";
        Map<String, Object> params = new HashMap<>();
        params.put("personGroupId", personGroupId);
        params.put("dismissalDatePlusOne", DateUtils.addDays(dismissalDate, 1));
        AssignmentExt assignmentExt = commonService.emQueryFirstResult(
                AssignmentExt.class,
                queryString,
                params,
                "assignment.full");
        if (assignmentExt != null) {
            assignmentExt.setWriteHistory(false);
        }
        return assignmentExt;
    }

    protected AssignmentExt getOldAssignment(UUID personGroupId, Date dismissalDate) {
        String queryString = "SELECT e from base$AssignmentExt e\n" +
                "   WHERE e.personGroup.id = :personGroupId\n" +
                "   AND e.endDate = :dismissalDate";
        Map<String, Object> params = new HashMap<>();
        params.put("personGroupId", personGroupId);
        params.put("dismissalDate", dismissalDate);
        AssignmentExt assignmentExt = commonService.emQueryFirstResult(
                AssignmentExt.class,
                queryString,
                params,
                "assignment.full");
        assignmentExt.setWriteHistory(false);
        return assignmentExt;
    }
}