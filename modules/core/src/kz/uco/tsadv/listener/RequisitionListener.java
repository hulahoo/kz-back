package kz.uco.tsadv.listener;

import com.haulmont.chile.core.model.MetaProperty;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.PersistenceTools;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.core.listener.AfterInsertEntityListener;
import com.haulmont.cuba.core.listener.AfterUpdateEntityListener;
import com.haulmont.cuba.core.listener.BeforeUpdateEntityListener;
import kz.uco.base.entity.extend.UserExt;
import kz.uco.base.service.NotificationService;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.personal.model.OrganizationHrUser;
import kz.uco.tsadv.modules.recruitment.enums.RequisitionStatus;
import kz.uco.tsadv.modules.recruitment.model.*;
import kz.uco.tsadv.service.EmployeeService;
import kz.uco.tsadv.service.RecruitmentService;
import kz.uco.uactivity.entity.ActivityType;
import kz.uco.uactivity.entity.StatusEnum;
import kz.uco.uactivity.service.ActivityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component("tsadv_RequisitionListener")
public class RequisitionListener implements AfterUpdateEntityListener<Requisition>, AfterInsertEntityListener<Requisition>, BeforeUpdateEntityListener<Requisition> {
    protected static final Logger log = LoggerFactory.getLogger(RequisitionListener.class);
    protected static final List<String> excludeProperties = Arrays.asList("requisition", "viewCount");

    @Inject
    protected Persistence persistence;

    @Inject
    protected Metadata metadata;

    @Inject
    protected DataManager dataManager;

    @Inject
    protected NotificationService notificationService;

    @Inject
    protected CommonService commonService;

    @Inject
    protected EmployeeService employeeService;

    /*@Inject
    private GlobalConfig globalConfig;

    @Inject
    private Events events;

    @Inject
    private Configuration configuration;*/

    @Inject
    protected ActivityService activityService;

    @Inject
    protected RecruitmentService recruitmentService;

    @Override
    public void onAfterUpdate(Requisition entity, Connection connection) {
        PersistenceTools persistenceTools = persistence.getTools();
        if (persistenceTools.isDirty(entity, "requisitionStatus", "openedPositionsCount")) {
            try (Transaction tx = persistence.createTransaction()) {
                EntityManager em = persistence.getEntityManager();
                em.persist(createRequisitionHistory(entity));
                tx.commit();
            }
        }

        if (false) { //closed because of task AAR-73
            if (persistenceTools.isDirty(entity, "openedPositionsCount")) {
                try {
                    Map<String, Object> params = new HashMap<>();
                    params.put("requisition", entity);

                    UserExt recruiterUser = commonService.getEntity(UserExt.class,
                            "select e from base$UserExt e " +
                                    " join tsadv$UserExtPersonGroup u on u.userExt.id = e.id " +
                                    "where u.personGroup.id = :personGroupId",
                            Collections.singletonMap("personGroupId", entity.getRecruiterPersonGroup().getId()),
                            "user.browse");

                    params.put("user", recruiterUser);
                    params.put("requisitionNameRu", entity.getNameForSiteLang1());
                    params.put("requisitionNameKz", entity.getNameForSiteLang2());
                    params.put("requisitionNameEn", entity.getNameForSiteLang3());
                    notificationService.sendParametrizedNotification("requisition.changed.notification", recruiterUser, params);

                    for (OrganizationHrUser hrUser : employeeService.getHrUsers(entity.getOrganizationGroup().getId(), "RECRUITING_MANAGER")) {
                        UserExt recruiterManagerUser = commonService.getEntity(UserExt.class,
                                "select e from base$UserExt e where e.id = :userId",
                                Collections.singletonMap("userId", hrUser.getUser().getId()),
                                "user.browse");
                        params.put("user", recruiterManagerUser);
                        notificationService.sendParametrizedNotification("requisition.changed.notification", recruiterManagerUser, params);
                    }
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
        }

        if (persistenceTools.isDirty(entity, "recruiterPersonGroup")) {
            if (entity.getRecruiterPersonGroup() != null) {
                try (Transaction tx = persistence.createTransaction()) {
                    Map<String, Object> params = new HashMap<>();
                    UserExt recruiterManagerUser = employeeService.getUserExtByPersonGroupId(entity.getRecruiterPersonGroup().getId());
                    params.put("jobName", entity.getJobGroup().getJob() != null ? entity.getJobGroup().getJob().getJobName() : "");
                    params.put("code", entity.getCode());
                    params.put("requisitionNameRu", entity.getNameForSiteLang1());
                    params.put("requisitionNameKz", entity.getNameForSiteLang2());
                    params.put("requisitionNameEn", entity.getNameForSiteLang3());
                    activityService.createActivity(
                            recruiterManagerUser,
                            employeeService.getSystemUser(),
                            commonService.getEntity(ActivityType.class, "NOTIFICATION"),
                            StatusEnum.active,
                            "description",
                            null,
                            new Date(),
                            null,
                            "",
                            entity.getId(),
                            "requisition.notify.recruiter",
                            params);
                    notificationService.sendParametrizedNotification("requisition.notify.recruiter", recruiterManagerUser, params);
                    tx.commit();
                }
            }
        }

        if (RequisitionStatus.OPEN.getId().equals(persistenceTools.getOldValue(entity, "requisitionStatus"))) {
            if (persistenceTools.isDirty(entity, metadata
                    .getClassNN("tsadv$RequisitionTmp")
                    .getOwnProperties()
                    .stream()
                    .map(MetaProperty::getName)
                    .filter(p -> excludeProperties.indexOf(p) < 0)
                    .collect(Collectors.toList())
                    .toArray(new String[0])))
                try (Transaction tx = persistence.createTransaction()) {
                    EntityManager em = persistence.getEntityManager();
                    em.persist(createRequisitionTmp(entity));
                    tx.commit();
                }
        }
    }


    protected RequisitionHistory createRequisitionHistory(Requisition requisition) {
        RequisitionHistory requisitionHistory = metadata.create(RequisitionHistory.class);

        requisitionHistory.setRequisition(requisition);
        requisitionHistory.setStatus(requisition.getRequisitionStatus());
        requisitionHistory.setReason(requisition.getReason());
        requisitionHistory.setOpenedPositionsCount(requisition.getOpenedPositionsCount());
        return requisitionHistory;
    }

    protected RequisitionTmp createRequisitionTmp(Requisition requisition) {
        RequisitionTmp requisitionTmp = metadata.create(RequisitionTmp.class);
        requisitionTmp.setRequisition(requisition);
        PersistenceTools persistenceTools = persistence.getTools();

        Set<String> dirtyFields = persistenceTools.getDirtyFields(requisition);

        for (String field : metadata
                .getClassNN("tsadv$RequisitionTmp")
                .getOwnProperties()
                .stream()
                .map(MetaProperty::getName)
                .filter(p -> excludeProperties.indexOf(p) < 0)
                .collect(Collectors.toList())) {
            requisitionTmp._persistence_set(field,
                    dirtyFields.contains(field) ?
                            persistenceTools.getOldValue(requisition, field) :
                            requisition._persistence_get(field));
        }

        return requisitionTmp;
    }

    @Override
    public void onAfterInsert(Requisition entity, Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) connection.commit();
            try (Transaction tx = persistence.createTransaction()) {
                EntityManager em = persistence.getEntityManager();
                em.persist(createRequisitionHistory(entity));
                tx.commit();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (entity.getHiringSteps() == null || entity.getHiringSteps().isEmpty()) {
            List<HiringStep> hiringSteps = commonService.getEntities(HiringStep.class, "select e from tsadv$HiringStep e " +
                            " where e.default_ = true and current_date between e.startDate and e.endDate ",
                    null, "hiringStep-for-listener");
            List<RequisitionHiringStep> requisitionHiringSteps = new ArrayList<>();
            for (HiringStep hiringStep : hiringSteps) {
                RequisitionHiringStep requisitionHiringStep = metadata.create(RequisitionHiringStep.class);
                requisitionHiringStep.setHiringStep(hiringStep);
                requisitionHiringStep.setOrder(hiringStep.getOrderDefault());
                requisitionHiringStep.setRequisition(entity);
                requisitionHiringSteps.add(requisitionHiringStep);
            }
            dataManager.commit(new CommitContext(requisitionHiringSteps));
        }
    }

    @Override
    public void onBeforeUpdate(Requisition entity, EntityManager entityManager) {
        PersistenceTools persistenceTools = persistence.getTools();

        if (entity.getRequisitionStatus() == RequisitionStatus.OPEN
                && !persistenceTools.isDirty(entity, "requisitionStatus")) {
            boolean changeStatus = recruitmentService.getIsChangeStatus(entity, AppBeans.get(UserSessionSource.class).getUserSession().getUser().getLogin());

            if (changeStatus && persistenceTools.isDirty(entity, metadata
                    .getClassNN("tsadv$RequisitionTmp")
                    .getOwnProperties()
                    .stream()
                    .map(MetaProperty::getName)
                    .filter(p -> excludeProperties.indexOf(p) < 0)
                    .collect(Collectors.toList())
                    .toArray(new String[0])))
                entity.setRequisitionStatus(RequisitionStatus.DRAFT);
        }
    }


}