package kz.uco.tsadv.service;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Query;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;
import kz.uco.base.notification.NotificationSenderAPI;
import kz.uco.tsadv.modules.administration.TsadvUser;
import kz.uco.tsadv.modules.learning.enums.CertificationPeriod;
import kz.uco.tsadv.modules.learning.enums.CertificationStatus;
import kz.uco.tsadv.modules.learning.enums.EnrollmentStatus;
import kz.uco.tsadv.modules.learning.model.CertificationEnrollment;
import kz.uco.tsadv.modules.learning.model.Enrollment;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.*;

@Service(CertificationService.NAME)
public class CertificationServiceBean implements CertificationService {

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");

    @Inject
    private Persistence persistence;
    @Inject
    private NotificationSenderAPI notificationSenderAPI;
    @Inject
    private Metadata metadata;
    @Inject
    private DataManager dataManager;

    @Override
    public void checkCertificationCourse() {
        List<CertificationEnrollment> listEnrollments = loadCertificationEnrollments();
        Date currentDate = new Date();

        List<CertificationEnrollment> completedEnrollments = new ArrayList<>();

        for (CertificationEnrollment model : listEnrollments) {

            Date nextDate = model.getCertification().getPeriod().calculateNextDate(model.getStartDate());

            Date notifyDate = DateUtils.addDays(nextDate, -model.getCertification().getNotifyDay());

            boolean notify = DateUtils.isSameDay(currentDate, notifyDate);
            if (notify) {
                Enrollment enrollment = createEnrollment(model);
                sendNotification(enrollment, "certification.notification", null);
            }

            Enrollment enrollment = null;
            if (DateUtils.isSameDay(currentDate, nextDate)) {
                enrollment = getEnrollment(model);

                Map<String, Object> params = new HashMap<>();
                params.put("lifeDay", model.getCertification().getLifeDay());

                switch (enrollment.getStatus()) {
                    case REQUIRED_SETTING: {
                        sendNotification(enrollment, "certification.max.life", params);
                    }
                    case REQUEST:
                    case APPROVED:
                    case WAITLIST: {
                        sendNotification(enrollment, "certification.must.complete", params);
                    }
                }
            }

            Date maxLifeDate = DateUtils.addDays(nextDate, model.getCertification().getLifeDay());

            boolean generateNext = DateUtils.isSameDay(currentDate, maxLifeDate);


            if (generateNext) {
                if (enrollment == null) enrollment = getEnrollment(model);

                if (enrollment.getStatus().equals(EnrollmentStatus.COMPLETED)) {
                    createCertificationEnrollment(model, enrollment, nextDate);
                } else if (!enrollment.getStatus().equals(EnrollmentStatus.CANCELLED)) {
                    enrollment.setStatus(EnrollmentStatus.CANCELLED);
                    enrollment.setReason("Просрочено");
                    dataManager.commit(enrollment);

                    sendNotification(enrollment, "certification.expired", null);
                }

                model.setStatus(CertificationStatus.PAST);
                completedEnrollments.add(model);
            }
        }

        if (!completedEnrollments.isEmpty()) {
            for (CertificationEnrollment certificationEnrollment : completedEnrollments) {
                dataManager.commit(certificationEnrollment);
            }
        }
    }

    private void sendNotification(Enrollment enrollment, String notificationCode, Map<String, Object> params) {
        TsadvUser userExt = getUserExt(enrollment);

        Map<String, Object> maps = params == null ? new HashMap<>() : params;
        maps.put("user", userExt);
        maps.put("enrollment", enrollment);

        notificationSenderAPI.sendParametrizedNotification(notificationCode, userExt, maps);
    }

    private TsadvUser getUserExt(Enrollment enrollment) {
        LoadContext<TsadvUser> loadContext = LoadContext.create(TsadvUser.class);
        loadContext.setQuery(LoadContext.createQuery("select e from sec$User e where e.personGroup.id = :pId")
                .setParameter("pId", enrollment.getPersonGroup().getId()))
                .setView("user.edit");
        return dataManager.load(loadContext);
    }

    private CertificationEnrollment createCertificationEnrollment(CertificationEnrollment certificationEnrollment, Enrollment enrollment, Date nextDate) {
        CertificationEnrollment nextCertEnrollment = metadata.create(CertificationEnrollment.class);
        nextCertEnrollment.setCertification(certificationEnrollment.getCertification());
        nextCertEnrollment.setPersonGroup(certificationEnrollment.getPersonGroup());
        nextCertEnrollment.setStatus(CertificationStatus.ACTIVE);

        switch (certificationEnrollment.getCertification().getCalculateType()) {
            case FIRST_ATTEMPT: {
                CertificationPeriod period = certificationEnrollment.getCertification().getPeriod();
                Date nextNextDate = null;
                if (period != null) {
                    nextNextDate = period.calculateNextDate(nextDate);
                }
                nextCertEnrollment.setNextDate(nextNextDate);
                break;
            }
            case LAST_DATE: {
                Date lastAttemptDate = getLastAttempt(enrollment);
                if (lastAttemptDate != null) nextDate = lastAttemptDate;
                break;
            }
        }

        nextCertEnrollment.setStartDate(nextDate);

        return dataManager.commit(nextCertEnrollment);
    }

    private Date getLastAttempt(Enrollment enrollment) {
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            Query query = em.createNativeQuery(
                    "select max(a.attempt_date) from tsadv_course_section_attempt a " +
                            "where a.enrollment_id = ?1 and a.SUCCESS = ?2");
            query.setParameter(1, enrollment.getId());
            query.setParameter(2, Boolean.TRUE);

            Object object = query.getSingleResult();
            Date maxDate = null;
            if (object != null) {
                maxDate = (Date) object;
            }
            tx.end();
            return maxDate;
        }
    }

    private Enrollment createEnrollment(CertificationEnrollment certificationEnrollment) {
        Enrollment enrollment = metadata.create(Enrollment.class);
        enrollment.setStatus(EnrollmentStatus.REQUIRED_SETTING);
        enrollment.setCertificationEnrollment(certificationEnrollment);
        enrollment.setCourse(certificationEnrollment.getCertification().getCourse());
        enrollment.setPersonGroup(certificationEnrollment.getPersonGroup());
        enrollment.setDate(new Date());
        return dataManager.commit(enrollment);
    }

    private Enrollment getEnrollment(CertificationEnrollment certificationEnrollment) {
        LoadContext<Enrollment> loadContext = LoadContext.create(Enrollment.class);
        loadContext.setQuery(LoadContext.createQuery(
                "select e from tsadv$Enrollment e " +
                        "where e.certificationEnrollment.id = :cId")
                .setParameter("cId", certificationEnrollment.getId()))
                .setView("enrollment.standardschedule");
        return dataManager.load(loadContext);
    }

    private List<CertificationEnrollment> loadCertificationEnrollments() {
        LoadContext<CertificationEnrollment> loadContext = LoadContext.create(CertificationEnrollment.class);
        LoadContext.Query query = LoadContext.createQuery(
                "select e from tsadv$CertificationEnrollment e " +
                        "where e.status = :status");
        query.setParameter("status", CertificationStatus.ACTIVE);
        loadContext.setQuery(query);
        loadContext.setView("certificationEnrollment.standardschedule");
        return dataManager.loadList(loadContext);
    }
}