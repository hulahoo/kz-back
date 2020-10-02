package kz.uco.tsadv.listener;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.PersistenceTools;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.listener.AfterInsertEntityListener;
import com.haulmont.cuba.core.listener.AfterUpdateEntityListener;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.base.entity.core.notification.NotificationTemplate;
import kz.uco.base.entity.extend.UserExt;
import kz.uco.base.service.NotificationService;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.administration.enums.HiringStepType;
import kz.uco.tsadv.modules.personal.model.OrganizationHrUser;
import kz.uco.tsadv.modules.recruitment.enums.InterviewStatus;
import kz.uco.tsadv.modules.recruitment.model.Interview;
import kz.uco.tsadv.modules.recruitment.model.InterviewHistory;
import kz.uco.tsadv.service.EmployeeService;
import kz.uco.tsadv.service.OrganizationHrUserService;
import kz.uco.uactivity.entity.ActivityType;
import kz.uco.uactivity.entity.StatusEnum;
import kz.uco.uactivity.service.ActivityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Component("tsadv_InterviewListener")
public class InterviewListener implements AfterInsertEntityListener<Interview>, AfterUpdateEntityListener<Interview> {

    private static final Logger log = LoggerFactory.getLogger(InterviewListener.class);

    @Inject
    protected Persistence persistence;

    @Inject
    protected Metadata metadata;

    @Inject
    protected DataManager dataManager;

    protected UserSession userSession;
    protected NotificationService notificationService;
    protected ActivityService activityService;
    protected CommonService commonService;
    protected OrganizationHrUserService organizationHrUserService;
    protected EmployeeService employeeService;

    @Override
    public void onAfterInsert(Interview entity, Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) connection.commit();

            try (Transaction tx = persistence.createTransaction()) {
                EntityManager em = persistence.getEntityManager();
                em.persist(createInterviewHistory(entity));
                tx.commit();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onAfterUpdate(Interview entity, Connection connection) {
        PersistenceTools persistenceTools = persistence.getTools();
        if (persistenceTools.isDirty(entity, "interviewStatus")) {
            try (Transaction tx = persistence.createTransaction()) {
                EntityManager em = persistence.getEntityManager();
                em.persist(createInterviewHistory(entity));
                tx.commit();
            }
            try (Transaction tx = persistence.createTransaction()) {
                sendNotification(entity);
            }
        }
    }

    protected void sendNotification(Interview interview) {
        try {
            String CANDIDATE_PASSED_TEST_NOTIFICATION_TEMPLATE_CODE = "candidatePassedTest";

            notificationService = AppBeans.get(NotificationService.class);
            activityService = AppBeans.get(ActivityService.class);
            commonService = AppBeans.get(CommonService.class);
            organizationHrUserService = AppBeans.get(OrganizationHrUserService.class);
            employeeService = AppBeans.get(EmployeeService.class);
            NotificationTemplate notificationTemplate =
                    notificationService.getNotificationTemplate(CANDIDATE_PASSED_TEST_NOTIFICATION_TEMPLATE_CODE);
            if (notificationTemplate == null) {
                log.warn("Notification Template '" + CANDIDATE_PASSED_TEST_NOTIFICATION_TEMPLATE_CODE + "' is not found");
                return;
            }
            HiringStepType hiringStepType = getSafeValue(() ->
                    interview.getRequisitionHiringStep().getHiringStep().getType(), null);
            if (interview.getInterviewStatus().equals(InterviewStatus.COMPLETED) &&
                    hiringStepType.equals(HiringStepType.test)) {
                UserExt creator = employeeService.getUserByLogin(interview.getCreatedBy());
                Map<String, Object> mapForNotification = new HashMap<>();
                mapForNotification.put(
                        "candidateFullName",
                        getSafeValue(() -> interview.getJobRequest().getCandidatePersonGroup().getFullName(), ""));
                mapForNotification.put(
                        "vacancyName",
                        getSafeValue(
                                () -> interview.getJobRequest().getRequisition().getJobGroup().getJob().getJobName(),
                                ""));
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                mapForNotification.put(
                        "datePassedTest",
                        getSafeValue(() -> dateFormat.format(interview.getInterviewDate()), ""));
                List<OrganizationHrUser> organizationHrUsers =
                        organizationHrUserService.getHrUsers(
                                interview.getJobRequest().getRequisition().getOrganizationGroup().getId(),
                                "RECRUITING_SPECIALIST",
                                null);
                for (OrganizationHrUser organizationHrUser : organizationHrUsers) {
                    notificationService.sendParametrizedNotification(CANDIDATE_PASSED_TEST_NOTIFICATION_TEMPLATE_CODE,
                            organizationHrUser.getUser(), mapForNotification);
                    activityService.createActivity(
                            notificationTemplate.getName(),
                            organizationHrUser.getUser(),
                            creator,
                            commonService.getEntity(ActivityType.class, "NOTIFICATION"),
                            StatusEnum.active,
                            "description",
                            null,
                            new Date(),
                            null,
                            null,
                            interview.getId(),
                            CANDIDATE_PASSED_TEST_NOTIFICATION_TEMPLATE_CODE,
                            mapForNotification);
                }
            }
        } catch (Exception ex) {
            log.error("Error is occurred in InterviewListener.sendNotification(" + interview + "): ", ex);
        }
    }

    private InterviewHistory createInterviewHistory(Interview interview) {
        InterviewHistory interviewHistory = metadata.create(InterviewHistory.class);
        interviewHistory.setInterview(interview);
        interviewHistory.setInterviewStatus(interview.getInterviewStatus());
        return interviewHistory;
    }

    /**
     * Возвращает либо значение выражения (getObject1.getObject2....)
     * либо заданное значение по умолчанию, если в цепочке какой-то объект null
     */
    protected static <T> T getSafeValue(Supplier<T> statement, T defaultValue) {
        try {
            return statement.get();
        } catch (NullPointerException exc) {
            return defaultValue;
        }
    }

}