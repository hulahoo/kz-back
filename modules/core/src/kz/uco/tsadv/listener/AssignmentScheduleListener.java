package kz.uco.tsadv.listener;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.PersistenceTools;
import com.haulmont.cuba.core.listener.*;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.IntegrationException;
import kz.uco.tsadv.config.IntegrationConfig;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.group.AssignmentGroupExt;
import kz.uco.tsadv.modules.timesheet.model.AssignmentSchedule;
import kz.uco.tsadv.service.AssignmentScheduleDividerService;
import kz.uco.tsadv.service.DatesService;
import kz.uco.tsadv.service.IntegrationService;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.sql.Connection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component("tsadv_AssignmentScheduleListener")
public class AssignmentScheduleListener implements AfterDeleteEntityListener<AssignmentSchedule>,
        AfterInsertEntityListener<AssignmentSchedule>,
        AfterUpdateEntityListener<AssignmentSchedule>,
        BeforeUpdateEntityListener<AssignmentSchedule>,
        BeforeDeleteEntityListener<AssignmentSchedule>,
        BeforeInsertEntityListener<AssignmentSchedule> {

    @Inject
    private IntegrationConfig integrationConfig;

    @Inject
    private IntegrationService integrationService;

    @Inject
    private CommonService commonService;

    @Inject
    private DatesService datesService;

    @Inject
    private PersistenceTools persistenceTools;

    @Inject
    private AssignmentScheduleDividerService assignmentScheduleDividerService;

    @Override
    public void onBeforeInsert(AssignmentSchedule entity, EntityManager entityManager) {
        checkAndChangeOldAssignmentSchedules(entity);
    }

    @Override
    public void onAfterDelete(AssignmentSchedule entity, Connection connection) {
        if (!this.integrationConfig.getIsIntegrationOff()) {
            try {
                this.integrationService.deleteAssignmentSchedule(entity, connection);
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
    public void onAfterInsert(AssignmentSchedule entity, Connection connection) {
        if (!this.integrationConfig.getIsIntegrationOff()) {
            try {
                this.integrationService.createAssignmentSchedule(entity, connection);
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
    public void onAfterUpdate(AssignmentSchedule entity, Connection connection) {
        if (!this.integrationConfig.getIsIntegrationOff()) {
            try {
                this.integrationService.updateAssignmentSchedule(entity, connection);
            } catch (Exception e) {
                if (e.getCause() != null) {
                    throw new IntegrationException(e.getCause().getMessage());
                } else {
                    throw new IntegrationException(e.getMessage());
                }
            }
        }
    }

    private void checkAndChangeOldAssignmentSchedules(AssignmentSchedule assignmentScheduleNotToChange) {
        AssignmentGroupExt assignmentGroup = assignmentScheduleNotToChange.getAssignmentGroup();
        if (assignmentGroup != null) {
            List<AssignmentSchedule> forChange = assignmentScheduleDividerService.loadOldAssignmentSchedules(assignmentScheduleNotToChange);
            for (AssignmentSchedule assignmentScheduleToChange : forChange) {
                if (!assignmentScheduleNotToChange.equals(assignmentScheduleToChange)) {
                    assignmentScheduleDividerService.changeAssignmentSchedule(assignmentScheduleToChange, assignmentScheduleNotToChange);
                }
            }
        }
    }

    @Override
    public void onBeforeUpdate(AssignmentSchedule entity, EntityManager entityManager) {
        Set<String> dirtyFields = persistenceTools.getDirtyFields(entity);
        if (dirtyFields.contains("startDate")) {
            Date oldStartDate = (Date) persistenceTools.getOldValue(entity, "startDate");
            if (entity.getStartDate().after(entity.getEndDate())) {
                onBeforeUpdateForStartDateGreaterThanEndDate(entity, entityManager, oldStartDate);
            } else {
                if (oldStartDate != null && entity.getStartDate().after(oldStartDate))
                    onBeforeUpdateForIncreaseStartDate(entity, entityManager, oldStartDate);
                else if (oldStartDate != null) {
                    onBeforeUpdateForDecreaseStartDate(entity, entityManager);
                }
            }
        }
    }

    protected void onBeforeUpdateForStartDateGreaterThanEndDate(AssignmentSchedule entity, EntityManager entityManager, Date oldStartDate) {
        List<AssignmentSchedule> assignmentScheduleList = getAssignmentScheduleList(entity, entity.getEndDate(), entity.getStartDate());
        Date endDate = null;
        for (AssignmentSchedule assignmentSchedule : assignmentScheduleList) {
            if (endDate == null || endDate.before(assignmentSchedule.getEndDate())) {
                endDate = assignmentSchedule.getEndDate();
            }
            entityManager.remove(assignmentSchedule);
        }
        entity.setEndDate(Optional.ofNullable(endDate).orElse(CommonUtils.getEndOfTime()));
        onBeforeUpdateForIncreaseStartDate(entity, entityManager, oldStartDate);
    }

    protected void onBeforeUpdateForDecreaseStartDate(AssignmentSchedule entity, EntityManager entityManager) {
        Date startDate = entity.getStartDate();
        Date endDate = entity.getEndDate();
        List<AssignmentSchedule> assignmentScheduleList = getAssignmentScheduleList(entity, startDate, endDate);

        for (AssignmentSchedule assignmentSchedule : assignmentScheduleList) {
            if (assignmentSchedule.equals(entity)) continue;
            if (datesService.between(startDate, endDate, assignmentSchedule.getStartDate())
                    && datesService.between(startDate, endDate, assignmentSchedule.getEndDate())) {
                entityManager.remove(assignmentSchedule);
            } else {
                if (!assignmentSchedule.getEndDate().before(startDate)) {
                    assignmentSchedule.setEndDate(DateUtils.addDays(startDate, -1));
                } else {
                    assignmentSchedule.setStartDate(DateUtils.addDays(endDate, 1));
                }
                entityManager.merge(assignmentSchedule);
            }
        }
    }

    protected List<AssignmentSchedule> getAssignmentScheduleList(AssignmentSchedule entity, Date startDate, Date endDate) {
        return commonService.emQueryResultList(AssignmentSchedule.class,
                " select e from tsadv$AssignmentSchedule e " +
                        " where e.assignmentGroup.id = :assignmentGroupId" +
                        "       and not ( e.endDate < :startDate or e.startDate > :endDate ) " +
                        "       and e.id <> :id ",
                ParamsMap.of("assignmentGroupId", entity.getAssignmentGroup().getId(),
                        "startDate", startDate,
                        "endDate", endDate,
                        "id", entity.getId()),
                "assignmentSchedule.view");
    }

    protected void onBeforeUpdateForIncreaseStartDate(AssignmentSchedule entity, EntityManager entityManager, Date oldStartDate) {
        AssignmentSchedule assignmentSchedule = commonService.getEntity(AssignmentSchedule.class,
                "select o from tsadv$AssignmentSchedule o " +
                        "   where o.assignmentGroup.id = :assignmentGroupId " +
                        "     and o.endDate = :endDate",
                ParamsMap.of("assignmentGroupId", entity.getAssignmentGroup().getId(),
                        "endDate", DateUtils.addDays(oldStartDate, -1)), "assignmentSchedule.view");
        if (assignmentSchedule != null) {
            assignmentSchedule.setEndDate(DateUtils.addDays(entity.getStartDate(), -1));
            entityManager.merge(assignmentSchedule);
        }
    }

    @Override
    public void onBeforeDelete(AssignmentSchedule entity, EntityManager entityManager) {
        AssignmentSchedule aheadAssignmentSchedule = assignmentScheduleDividerService.getAheadAssignmentSchedule(entity);
        AssignmentSchedule behindAssignmentSchedule = assignmentScheduleDividerService.getBehindAssignmentSchedule(entity);
        Date newEndDate;

        if (aheadAssignmentSchedule != null) {
            if (behindAssignmentSchedule != null) {
                newEndDate = DateUtils.addDays(behindAssignmentSchedule.getStartDate(), -1);
            } else {
                newEndDate = CommonUtils.getEndOfTime();
            }
            aheadAssignmentSchedule.setEndDate(newEndDate);
            entityManager.merge(aheadAssignmentSchedule);
        }
    }
}