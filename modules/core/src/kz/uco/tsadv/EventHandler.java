package kz.uco.tsadv;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.TypedQuery;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.security.app.Authenticated;
import kz.uco.base.ApplicationStartEvent;
import kz.uco.uactivity.entity.ActivityType;
import kz.uco.uactivity.entity.WindowProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * @author Murat
 * @author adilbekov.yernar
 */
@Component("tsadv_EventHandler")
public class EventHandler {

    private static final Logger logger = LoggerFactory.getLogger(EventHandler.class);

    @Inject
    protected Metadata metadata;

    @Inject
    protected Persistence persistence;

    @SuppressWarnings("NullableProblems")
    @Authenticated
    @EventListener(ApplicationStartEvent.class)
    public void onApplicationStart() {
        persistence.callInTransaction(this::fillActivityType);
    }

    public Object fillActivityType(EntityManager em) {
        if (getCount(em, "ABSENCE_REQUEST_APPROVE") == 0) {
            ActivityType activityType = metadata.create(ActivityType.class);
            activityType.setCode("ABSENCE_REQUEST_APPROVE");
            activityType.setScreen("tsadv$AbsenceRequestNew.edit");
            activityType.setLangValue1("Утверждение / отклонение заявление на отпуск ");
            WindowProperty windowProperty = metadata.create(WindowProperty.class);
            windowProperty.setEntityName("tsadv$AbsenceRequest");
            windowProperty.setScreenName("tsadv$AbsenceRequestNew.edit");
            windowProperty.setViewName("absenceRequest.view");
            activityType.setWindowProperty(windowProperty);
            em.persist(windowProperty);
            em.persist(activityType);
        }
        if (getCount(em, "CERTIFICATE_REQUEST_APPROVE") == 0) {
            ActivityType activityType = metadata.create(ActivityType.class);
            activityType.setCode("CERTIFICATE_REQUEST_APPROVE");
            activityType.setScreen("tsadv_CertificateRequest.edit");
            activityType.setLangValue1("Утверждение / отклонение заявление на справку ");
            WindowProperty windowProperty = metadata.create(WindowProperty.class);
            windowProperty.setEntityName("tsadv_CertificateRequest");
            windowProperty.setScreenName("tsadv_CertificateRequest.edit");
            windowProperty.setViewName(View.LOCAL);
            activityType.setWindowProperty(windowProperty);
            em.persist(windowProperty);
            em.persist(activityType);
        }
        if (getCount(em, "SCHEDULE_OFFSETS_REQUEST_APPROVE") == 0) {
            ActivityType activityType = metadata.create(ActivityType.class);
            activityType.setCode("SCHEDULE_OFFSETS_REQUEST_APPROVE");
            activityType.setScreen("tsadv_ScheduleOffsetsRequestSsMyTeam.edit");
            activityType.setLangValue1("Утверждение / отклонение заявление на смену графика");
            WindowProperty windowProperty = metadata.create(WindowProperty.class);
            windowProperty.setEntityName("tsadv_ScheduleOffsetsRequest");
            windowProperty.setScreenName("tsadv_ScheduleOffsetsRequestSsMyTeam.edit");
            windowProperty.setViewName(View.LOCAL);
            activityType.setWindowProperty(windowProperty);
            em.persist(windowProperty);
            em.persist(activityType);
        }
        if (getCount(em, "ABSENCE_RVD_REQUEST_APPROVE") == 0) {
            ActivityType activityType = metadata.create(ActivityType.class);
            activityType.setCode("ABSENCE_RVD_REQUEST_APPROVE");
            activityType.setScreen("tsadv_AbsenceRvdRequest.edit");
            activityType.setLangValue1("Утверждение / отклонение заявление на РВД");
            WindowProperty windowProperty = metadata.create(WindowProperty.class);
            windowProperty.setEntityName("tsadv_AbsenceRvdRequest");
            windowProperty.setScreenName("tsadv_AbsenceRvdRequest.edit");
            windowProperty.setViewName(View.LOCAL);
            activityType.setWindowProperty(windowProperty);
            em.persist(windowProperty);
            em.persist(activityType);
        }
        if (getCount(em, "LEAVING_VACATION_REQUEST_APPROVE") == 0) {
            ActivityType activityType = metadata.create(ActivityType.class);
            activityType.setCode("LEAVING_VACATION_REQUEST_APPROVE");
            activityType.setScreen("tsadv$LeavingVacationRequest.edit");
            activityType.setLangValue1("Утверждение / отклонение заявление на выход из отпуска без сохранения " +
                    " заработной платы по уходу за ребенком до достижения им возраста трех лет");
            WindowProperty windowProperty = metadata.create(WindowProperty.class);
            windowProperty.setEntityName("tsadv$LeavingVacationRequest");
            windowProperty.setScreenName("tsadv$LeavingVacationRequest.edit");
            windowProperty.setViewName(View.LOCAL);
            activityType.setWindowProperty(windowProperty);
            em.persist(windowProperty);
            em.persist(activityType);
        }
        if (getCount(em, "ASSIGNED_PERFORMANCE_PLAN_APPROVE") == 0) {
            ActivityType activityType = metadata.create(ActivityType.class);
            activityType.setCode("ASSIGNED_PERFORMANCE_PLAN_APPROVE");
            activityType.setScreen("tsadv$AssignedPerformancePlan.edit");
            activityType.setLangValue1("Утверждение / отклонение заявление на ASSIGNED_PERFORMANCE_PLAN_APPROVE");
            WindowProperty windowProperty = metadata.create(WindowProperty.class);
            windowProperty.setEntityName("tsadv$AssignedPerformancePlan");
            windowProperty.setScreenName("tsadv$AssignedPerformancePlan.edit");
            windowProperty.setViewName(View.LOCAL);
            activityType.setWindowProperty(windowProperty);
            em.persist(windowProperty);
            em.persist(activityType);
        }
        if (getCount(em, "ABSENCE_FOR_RECALL_APPROVE") == 0) {
            ActivityType activityType = metadata.create(ActivityType.class);
            activityType.setCode("ABSENCE_FOR_RECALL_APPROVE");
            activityType.setScreen("tsadv_AbsenceForRecall.edit");
            activityType.setLangValue1("Утверждение / отклонение заявление на отзыв из отпуска");
            WindowProperty windowProperty = metadata.create(WindowProperty.class);
            windowProperty.setEntityName("tsadv_AbsenceForRecall");
            windowProperty.setScreenName("tsadv_AbsenceForRecall.edit");
            windowProperty.setViewName("absenceForRecall.edit");
            activityType.setWindowProperty(windowProperty);
            em.persist(windowProperty);
            em.persist(activityType);
        }
        if (getCount(em, "CHANGE_ABSENCE_DAYS_REQUEST_APPROVE") == 0) {
            ActivityType activityType = metadata.create(ActivityType.class);
            activityType.setCode("CHANGE_ABSENCE_DAYS_REQUEST_APPROVE");
            activityType.setScreen("tsadv_ChangeAbsenceDaysRequest.edit");
            activityType.setLangValue1("Утверждение / отклонение заявление на изменение дат отпуска");
            WindowProperty windowProperty = metadata.create(WindowProperty.class);
            windowProperty.setEntityName("tsadv_ChangeAbsenceDaysRequest");
            windowProperty.setScreenName("tsadv_ChangeAbsenceDaysRequest.edit");
            windowProperty.setViewName("changeAbsenceDaysRequest.edit");
            activityType.setWindowProperty(windowProperty);
            em.persist(windowProperty);
            em.persist(activityType);
        }
        if (getCount(em, "PERSON_DOCUMENT_REQUEST_APPROVE") == 0) {
            ActivityType activityType = metadata.create(ActivityType.class);
            activityType.setCode("PERSON_DOCUMENT_REQUEST_APPROVE");
            activityType.setScreen("tsadv_PersonDocumentRequest.edit");
            activityType.setLangValue1("Утверждение / отклонение заявление на документ");
            WindowProperty windowProperty = metadata.create(WindowProperty.class);
            windowProperty.setEntityName("tsadv_PersonDocumentRequest");
            windowProperty.setScreenName("tsadv_PersonDocumentRequest.edit");
            windowProperty.setViewName("portal.my-profile");
            activityType.setWindowProperty(windowProperty);
            em.persist(windowProperty);
            em.persist(activityType);
        }
        if (getCount(em, "NOTIFICATION") == 0) {
            ActivityType activityType = metadata.create(ActivityType.class);
            activityType.setCode("NOTIFICATION");
            activityType.setLangValue1("Уведомление");
            activityType.setLangValue3("Notification");
            em.persist(activityType);
        }
        /*if (getCount(em, "REQUISITION_APPROVE") == 0) {
            ActivityType activityType = metadata.create(ActivityType.class);
            activityType.setCode("REQUISITION_APPROVE");
            activityType.setScreen("tsadv$Requisition.edit");
            activityType.setLangValue1("Утверждение/отклонение вакансии рекрутер менеджером");
            WindowProperty windowProperty = metadata.create(WindowProperty.class);
            windowProperty.setEntityName("tsadv$Requisition");
            windowProperty.setScreenName("tsadv$Requisition.edit");
            windowProperty.setViewName("requisition.view");
            activityType.setWindowProperty(windowProperty);
            em.persist(windowProperty);
            em.persist(activityType);
        }
        if (getCount(em, "INTERVIEW_APPROVE") == 0) {
            ActivityType activityType = metadata.create(ActivityType.class);
            activityType.setCode("INTERVIEW_APPROVE");
            activityType.setScreen("tsadv$Interview.edit");
            activityType.setLangValue1("Принятие/отклонение приглашения на интервью кандидатом");
            WindowProperty windowProperty = metadata.create(WindowProperty.class);
            windowProperty.setEntityName("tsadv$Interview");
            windowProperty.setScreenName("tsadv$Interview.edit");
            windowProperty.setViewName("interview.view");
            activityType.setWindowProperty(windowProperty);
            em.persist(windowProperty);
            em.persist(activityType);
        }
        if (getCount(em, "OFFER_APPROVE") == 0) {
            ActivityType activityType = metadata.create(ActivityType.class);
            activityType.setCode("OFFER_APPROVE");
            activityType.setScreen("tsadv$Offer.edit");
            activityType.setLangValue1("Утверждение/отклонение предложения о работе руководителем");
            WindowProperty windowProperty = metadata.create(WindowProperty.class);
            windowProperty.setEntityName("tsadv$Offer");
            windowProperty.setScreenName("tsadv$Offer.edit");
            windowProperty.setViewName("offer.browse");
            activityType.setWindowProperty(windowProperty);
            em.persist(windowProperty);
            em.persist(activityType);
        }
        if (getCount(em, "KPI_APPROVE") == 0) {
            ActivityType activityType = metadata.create(ActivityType.class);
            activityType.setCode("KPI_APPROVE");
            activityType.setScreen("tsadv$MyAssignedGoal.browse");
            activityType.setLangValue1("Согласование KPI");
            WindowProperty windowProperty = metadata.create(WindowProperty.class);
            windowProperty.setEntityName("tsadv$MyAssignedGoal.browse");
            windowProperty.setScreenName("kms$PerformancePlanHeader");
            windowProperty.setViewName("performancePlanHeader-for-approve");
            activityType.setWindowProperty(windowProperty);
            em.persist(windowProperty);
            em.persist(activityType);
        }
        */
        if (getCount(em, "PERSONAL_DATA_REQUEST_APPROVE") == 0) {
            ActivityType activityType = metadata.create(ActivityType.class);
            activityType.setCode("PERSONAL_DATA_REQUEST_APPROVE");
            activityType.setScreen("tsadv$PersonalDataRequest.bpm");
            activityType.setLangValue1("Утверждение / отклонение измененных данных");
            WindowProperty windowProperty = metadata.create(WindowProperty.class);
            windowProperty.setEntityName("tsadv$PersonalDataRequest");
            windowProperty.setScreenName("tsadv$PersonalDataRequest.bpm");
            windowProperty.setViewName("personalDataRequest-view");
            activityType.setWindowProperty(windowProperty);
            em.persist(windowProperty);
            em.persist(activityType);
        }
        if (getCount(em, "ADDRESS_REQUEST_APPROVE") == 0) {
            ActivityType activityType = metadata.create(ActivityType.class);
            activityType.setCode("ADDRESS_REQUEST_APPROVE");
            activityType.setScreen("tsadv$AddressRequest.edit");
            activityType.setLangValue1("Утверждение/отклонение адреса");
            WindowProperty windowProperty = metadata.create(WindowProperty.class);
            windowProperty.setEntityName("tsadv$AddressRequest");
            windowProperty.setScreenName("tsadv$AddressRequest.edit");
            windowProperty.setViewName("addressRequest-view");
            activityType.setWindowProperty(windowProperty);
            em.persist(windowProperty);
            em.persist(activityType);
        }
        /*
        if (getCount(em, "POSITION_CHANGE_REQUEST_APPROVE") == 0) {
            ActivityType activityType = metadata.create(ActivityType.class);
            activityType.setCode("POSITION_CHANGE_REQUEST_APPROVE");
            activityType.setScreen("tsadv$PositionChangeRequest.edit");
            activityType.setLangValue1("Утверждение/отклонение штатных ед.");
            WindowProperty windowProperty = metadata.create(WindowProperty.class);
            windowProperty.setEntityName("tsadv$PositionChangeRequest");
            windowProperty.setScreenName("tsadv$PositionChangeRequest.edit");
            windowProperty.setViewName("positionChangeRequest.edit");
            activityType.setWindowProperty(windowProperty);
            em.persist(windowProperty);
            em.persist(activityType);
        }
        if (getCount(em, "POSITION_CHANGE_REQUEST_TYPE_CHANGE_APPROVE") == 0) {
            ActivityType activityType = metadata.create(ActivityType.class);
            activityType.setCode("POSITION_CHANGE_REQUEST_TYPE_CHANGE_APPROVE");
            activityType.setScreen("tsadv$PositionChangeRequestTypeChange.edit");
            activityType.setLangValue1("Утверждение/отклонение изменение штатных ед.");
            WindowProperty windowProperty = metadata.create(WindowProperty.class);
            windowProperty.setEntityName("tsadv$PositionChangeRequest");
            windowProperty.setScreenName("tsadv$PositionChangeRequestTypeChange.edit");
            windowProperty.setViewName("positionChangeRequest.edit");
            activityType.setWindowProperty(windowProperty);
            em.persist(windowProperty);
            em.persist(activityType);
        }
        if (getCount(em, "SALARY_REQUEST_APPROVE") == 0) {
            ActivityType activityType = metadata.create(ActivityType.class);
            activityType.setCode("SALARY_REQUEST_APPROVE");
            activityType.setScreen("tsadv$SalaryRequest.edit");
            activityType.setLangValue1("Утверждение / отклонение новой зарплаты");
            WindowProperty windowProperty = metadata.create(WindowProperty.class);
            windowProperty.setEntityName("tsadv$SalaryRequest");
            windowProperty.setScreenName("tsadv$SalaryRequest.edit");
            windowProperty.setViewName("salary-request.view");
            activityType.setWindowProperty(windowProperty);
            em.persist(windowProperty);
            em.persist(activityType);
        }
        if (getCount(em, "ASSIGNMENT_REQUEST_APPROVE") == 0) {
            ActivityType activityType = metadata.create(ActivityType.class);
            activityType.setCode("ASSIGNMENT_REQUEST_APPROVE");
            activityType.setScreen("tsadv$AssignmentRequest.edit");
            activityType.setLangValue1("Утверждение / отклонение назначение");
            WindowProperty windowProperty = metadata.create(WindowProperty.class);
            windowProperty.setEntityName("tsadv$AssignmentRequest");
            windowProperty.setScreenName("tsadv$AssignmentRequest.edit");
            windowProperty.setViewName("assignmentRequest-view");
            activityType.setWindowProperty(windowProperty);
            em.persist(windowProperty);
            em.persist(activityType);
        }
        if (getCount(em, "ASSIGNMENT_SALARY_REQUEST_APPROVE") == 0) {
            ActivityType activityType = metadata.create(ActivityType.class);
            activityType.setCode("ASSIGNMENT_SALARY_REQUEST_APPROVE");
            activityType.setScreen("tsadv$AssignmentSalaryRequest.edit");
            activityType.setLangValue1("Перевод с изменением ЗП");
            WindowProperty windowProperty = metadata.create(WindowProperty.class);
            windowProperty.setEntityName("tsadv$AssignmentSalaryRequest");
            windowProperty.setScreenName("tsadv$AssignmentSalaryRequest.edit");
            windowProperty.setViewName("assignmentSalaryRequest-view");
            activityType.setWindowProperty(windowProperty);
            em.persist(windowProperty);
            em.persist(activityType);
        }
        if (getCount(em, "TEMPORARY_TRANSLATION_REQUEST_APPROVE") == 0) {
            ActivityType activityType = metadata.create(ActivityType.class);
            activityType.setCode("TEMPORARY_TRANSLATION_REQUEST_APPROVE");
            activityType.setScreen("tsadv$TemporaryTranslationRequest.edit");
            activityType.setLangValue1("Временный перевод");
            activityType.setLangValue3("Temporary transfer");
            WindowProperty windowProperty = metadata.create(WindowProperty.class);
            windowProperty.setEntityName("tsadv$TemporaryTranslationRequest");
            windowProperty.setScreenName("tsadv$TemporaryTranslationRequest.edit");
            windowProperty.setViewName("temporaryTranslationRequest-view");
            activityType.setWindowProperty(windowProperty);
            em.persist(windowProperty);
            em.persist(activityType);
        }*/
        return em;
    }

    protected Long getCount(EntityManager em, String code) {
        TypedQuery<Long> query = em.createQuery("select count(e) from uactivity$ActivityType e" +
                " where e.code = :code", Long.class);
        query.setParameter("code", code);
        return query.getSingleResult();
    }
}
