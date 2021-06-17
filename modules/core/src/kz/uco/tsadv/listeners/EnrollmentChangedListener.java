package kz.uco.tsadv.listeners;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.TransactionalDataManager;
import com.haulmont.cuba.core.app.FileStorageAPI;
import com.haulmont.cuba.core.app.events.AttributeChanges;
import com.haulmont.cuba.core.app.events.EntityChangedEvent;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.*;
import com.haulmont.reports.app.service.ReportService;
import kz.uco.base.entity.shared.Person;
import kz.uco.base.service.NotificationSenderAPIService;
import kz.uco.tsadv.beans.validation.tdc.enrollment.EnrollmentValidation;
import kz.uco.tsadv.config.FrontConfig;
import kz.uco.tsadv.modules.administration.TsadvUser;
import kz.uco.tsadv.modules.learning.enums.EnrollmentStatus;
import kz.uco.tsadv.modules.learning.model.CourseCertificate;
import kz.uco.tsadv.modules.learning.model.CoursePersonNote;
import kz.uco.tsadv.modules.learning.model.Enrollment;
import kz.uco.tsadv.modules.learning.model.EnrollmentCertificateFile;
import kz.uco.tsadv.modules.performance.model.CourseTrainer;
import kz.uco.tsadv.service.LmsService;
import kz.uco.tsadv.service.OrganizationHrUserService;
import kz.uco.uactivity.entity.ActivityType;
import kz.uco.uactivity.entity.StatusEnum;
import kz.uco.uactivity.entity.WindowProperty;
import kz.uco.uactivity.service.ActivityService;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.inject.Inject;
import java.util.*;

@Component("tsadv_EnrollmentChangedListener")
public class EnrollmentChangedListener {
    @Inject
    protected NotificationSenderAPIService notificationSenderAPIService;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected LmsService lmsService;
    @Inject
    protected FileStorageAPI fileStorageAPI;
    @Inject
    protected ReportService reportService;
    @Inject
    protected Metadata metadata;
    @Inject
    protected GlobalConfig globalConfig;
    @Inject
    protected ActivityService activityService;
    @Inject
    protected FrontConfig frontConfig;
    @Inject
    private TransactionalDataManager transactionalDataManager;
    @Inject
    private List<EnrollmentValidation> enrollmentValidations;
    @Inject
    private OrganizationHrUserService organizationHrUserService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void beforeCommit(EntityChangedEvent<Enrollment, UUID> event) {
        AttributeChanges changes = event.getChanges();
        Enrollment enrollment;
        if (event.getType().equals(EntityChangedEvent.Type.UPDATED)) {
            enrollment = transactionalDataManager.load(event.getEntityId()).view("enrollment.for.course").one();
            if (event.getChanges().isChanged("status")) {
                Enrollment enrollmentFront = transactionalDataManager.load(event.getEntityId())
                        .view("enrollment.validation.status")
                        .one();
                for (EnrollmentValidation enrollmentValidation : enrollmentValidations) {
                    enrollmentValidation.validate(enrollmentFront);
                }

                for (String attribute : changes.getAttributes()) {

                    if (!attribute.equals("status")) continue;

                    Integer oldValue = changes.getOldValue(attribute);
                    if (!EnrollmentStatus.APPROVED.equals(EnrollmentStatus.fromId(oldValue))
                            && EnrollmentStatus.APPROVED.equals(enrollment.getStatus())) {

                        TsadvUser tsadvUser = dataManager.load(TsadvUser.class)
                                .query("select e from tsadv$UserExt e " +
                                        " where e.personGroup = :personGroup")
                                .parameter("personGroup", enrollment.getPersonGroup())
                                .list().stream().findFirst().orElse(null);
                        if (tsadvUser != null) {
                            Person person = enrollment.getPersonGroup() != null ? enrollment.getPersonGroup().getPerson() : null;
                            if (person != null) {
                                Map<String, Object> map = new HashMap<>();
                                String courseLink = "<a href=\"" + frontConfig.getFrontAppUrl()
                                        + "/course/"
                                        + enrollment.getCourse().getId()
                                        + "\" target=\"_blank\">%s " + "</a>";
                                String myCourseLink = "<a href=\"" + frontConfig.getFrontAppUrl()
                                        + "/my-course/"
                                        + "\" target=\"_blank\">%s " + "</a>";
                                map.put("linkRu", String.format(courseLink, enrollment.getCourse().getName()));
                                map.put("linkEn", String.format(courseLink, enrollment.getCourse().getName()));
                                map.put("linkKz", String.format(courseLink, enrollment.getCourse().getName()));
                                map.put("myLinkRu", String.format(myCourseLink, "Мои курсы"));
                                map.put("myLinkEn", String.format(myCourseLink, "My training course"));
                                map.put("myLinkKz", String.format(myCourseLink, "Менің курстарым"));
                                map.put("personFullNameRu", person.getFirstName() + " " + person.getLastName());
                                map.put("personFullNameEn", person.getFirstNameLatin() != null
                                        && !person.getFirstNameLatin().isEmpty()
                                        && person.getLastNameLatin() != null
                                        && !person.getLastNameLatin().isEmpty()
                                        ? person.getFirstNameLatin() + " " + person.getLastNameLatin()
                                        : person.getFirstName() + " " + person.getLastName());
                                map.put("courseName", enrollment.getCourse().getName());
                                activityService.createActivity(
                                        tsadvUser,
                                        tsadvUser,
                                        getActivityType(),
                                        StatusEnum.active,
                                        "description",
                                        null,
                                        new Date(),
                                        null,
                                        null,
                                        enrollment.getId(),
                                        "tdc.student.enrollmentApproved",
                                        map);
                                notificationSenderAPIService.sendParametrizedNotification("tdc.student.enrollmentApproved",
                                        tsadvUser, map);
                            }
                        }

                    } else if (!EnrollmentStatus.COMPLETED.equals(EnrollmentStatus.fromId(oldValue))
                            && EnrollmentStatus.COMPLETED.equals(enrollment.getStatus())) {

                        TsadvUser user = dataManager.load(TsadvUser.class)
                                .query("select e from tsadv$UserExt e " +
                                        " where e.personGroup = :personGroup")
                                .parameter("personGroup", enrollment.getPersonGroup())
                                .list().stream().findFirst().orElse(null);
                        Person person = enrollment.getPersonGroup() != null ? enrollment.getPersonGroup().getPerson() : null;
                        if (person != null) {
                            Map<String, Object> map = new HashMap<>();
                            String requestLink = "<a href=\"" + frontConfig.getFrontAppUrl()
                                    + "/learning-history/"
                                    + "\" target=\"_blank\">%s " + "</a>";
                            String feedbackLink = "<a href=\"" + frontConfig.getFrontAppUrl()
                                    + "/my-course/" + enrollment.getId().toString()
                                    + "\" target=\"_blank\">%s " + "</a>";
                            map.put("feedbackLinkRu", String.format(feedbackLink, "ЗДЕСЬ"));
                            map.put("feedbackLinkEn", String.format(feedbackLink, "CLICK"));
                            map.put("feedbackLinkKz", String.format(feedbackLink, "осы жерде"));
                            map.put("linkRu", String.format(requestLink, "История обучения"));
                            map.put("linkEn", String.format(requestLink, "Training History"));
                            map.put("linkKz", String.format(requestLink, "Оқу үлгерімі"));
                            map.put("courseName", enrollment.getCourse().getName());
                            map.put("personFullNameRu", person.getFirstName() + " " + person.getLastName());
                            map.put("personFullNameEn", person.getFirstNameLatin() != null
                                    && !person.getFirstNameLatin().isEmpty()
                                    && person.getLastNameLatin() != null
                                    && !person.getLastNameLatin().isEmpty()
                                    ? person.getFirstNameLatin() + " "
                                    + person.getLastNameLatin()
                                    : person.getFirstName() + " "
                                    + person.getLastName());

//                    sendNotificationCompleted(enrollment);
//                        CommitContext commitContext = new CommitContext();
                            CourseCertificate courseCertificate = enrollment.getCourse().getCertificate() != null
                                    && !enrollment.getCourse().getCertificate().isEmpty()
                                    ? enrollment.getCourse().getCertificate().get(0)
                                    : null;
                            if (courseCertificate != null) {

                                FileDescriptor fd = reportService.createAndSaveReport(courseCertificate.getCertificate(),
                                        ParamsMap.of("enrollment", enrollment), enrollment.getCourse().getName());


                                if (fd != null) {
                                    List<EnrollmentCertificateFile> ecfList = dataManager.load(EnrollmentCertificateFile.class)
                                            .query("select e from tsadv$EnrollmentCertificateFile e " +
                                                    " where e.enrollment = :enrollment ")
                                            .parameter("enrollment", enrollment)
                                            .view("enrollmentCertificateFile.with.certificateFile")
                                            .list();
                                    ecfList.forEach(transactionalDataManager::remove);

                                    EnrollmentCertificateFile ecf = metadata.create(EnrollmentCertificateFile.class);
                                    ecf.setCertificateFile(fd);
                                    ecf.setEnrollment(enrollment);

                                    transactionalDataManager.save(ecf);

                                    EmailAttachment[] emailAttachments = new EmailAttachment[0];
                                    emailAttachments = getEmailAttachments(fd, emailAttachments);
                                    activityService.createActivity(
                                            user,
                                            user,
                                            getActivityType(),
                                            StatusEnum.active,
                                            "description",
                                            null,
                                            new Date(),
                                            null,
                                            null,
                                            enrollment.getId(),
                                            "tdc.student.enrollmentClosed",
                                            map);

                                    notificationSenderAPIService.sendParametrizedNotification("tdc.student.enrollmentClosed",
                                            user, map, emailAttachments);
                                } else {
                                    activityService.createActivity(
                                            user,
                                            user,
                                            getActivityType(),
                                            StatusEnum.active,
                                            "description",
                                            null,
                                            new Date(),
                                            null,
                                            null,
                                            enrollment.getId(),
                                            "tdc.student.enrollmentClosed",
                                            map);
                                    notificationSenderAPIService.sendParametrizedNotification("tdc.student.enrollmentClosed",
                                            user, map);
                                }
                            } else {
                                activityService.createActivity(
                                        user,
                                        user,
                                        getActivityType(),
                                        StatusEnum.active,
                                        "description",
                                        null,
                                        new Date(),
                                        null,
                                        null,
                                        enrollment.getId(),
                                        "tdc.student.enrollmentClosed",
                                        map);
                                notificationSenderAPIService.sendParametrizedNotification("tdc.student.enrollmentClosed",
                                        user, map);
                            }

                            sendNotifyToTrainers(enrollment);
                            sendNotifyForLineManager(enrollment);
                        }
                    }
                }
            }
        } else if (event.getType().equals(EntityChangedEvent.Type.CREATED)) {

            enrollment = transactionalDataManager.load(event.getEntityId()).view("enrollment.for.course").one();

            if (EnrollmentStatus.APPROVED.equals(enrollment.getStatus())) {

                TsadvUser tsadvUser = dataManager.load(TsadvUser.class)
                        .query("select e from tsadv$UserExt e " +
                                " where e.personGroup = :personGroup")
                        .parameter("personGroup", enrollment.getPersonGroup())
                        .list().stream().findFirst().orElse(null);
                if (tsadvUser != null) {
                    Person person = enrollment.getPersonGroup() != null ? enrollment.getPersonGroup().getPerson() : null;
                    if (person != null) {
                        Map<String, Object> map = new HashMap<>();
                        String courseLink = "<a href=\"" + frontConfig.getFrontAppUrl()
                                + "/course/"
                                + enrollment.getCourse().getId()
                                + "\" target=\"_blank\">%s " + "</a>";
                        String myCourseLink = "<a href=\"" + frontConfig.getFrontAppUrl()
                                + "/my-course/"
                                + "\" target=\"_blank\">%s " + "</a>";
                        map.put("linkRu", String.format(courseLink, enrollment.getCourse().getName()));
                        map.put("linkEn", String.format(courseLink, enrollment.getCourse().getName()));
                        map.put("linkKz", String.format(courseLink, enrollment.getCourse().getName()));
                        map.put("myLinkRu", String.format(myCourseLink, "Мои курсы"));
                        map.put("myLinkEn", String.format(myCourseLink, "My training course"));
                        map.put("myLinkKz", String.format(myCourseLink, "Менің курстарым"));
                        map.put("personFullNameRu", person.getFirstName() + " " + person.getLastName());
                        map.put("personFullNameEn", person.getFirstNameLatin() != null
                                && !person.getFirstNameLatin().isEmpty()
                                && person.getLastNameLatin() != null
                                && !person.getLastNameLatin().isEmpty()
                                ? person.getFirstNameLatin() + " " + person.getLastNameLatin()
                                : person.getFirstName() + " " + person.getLastName());
                        map.put("courseName", enrollment.getCourse().getName());
                        activityService.createActivity(
                                tsadvUser,
                                tsadvUser,
                                getActivityType(),
                                StatusEnum.active,
                                "description",
                                null,
                                new Date(),
                                null,
                                null,
                                enrollment.getId(),
                                "tdc.student.enrollmentApproved",
                                map);
                        notificationSenderAPIService.sendParametrizedNotification("tdc.student.enrollmentApproved",
                                tsadvUser, map);
                        sendNotification(enrollment, "tdc.trainer.newEnrollment");
                    }
                }
            } else {
                sendNotification(enrollment, "tdc.trainer.newEnrollment");
            }
        } else if (event.getType().equals(EntityChangedEvent.Type.DELETED)) {
            Enrollment enrollmentFront = transactionalDataManager
                    .load(event.getEntityId())
                    .softDeletion(false)
                    .view(new View(Enrollment.class).addProperty("course").addProperty("personGroup"))
                    .one();

            transactionalDataManager.load(CoursePersonNote.class)
                    .query("select e from tsadv_CoursePersonNote e " +
                            " where e.course.id = :courseId and e.personGroup.id = :personGroupId  ")
                    .parameter("courseId", enrollmentFront.getCourse().getId())
                    .parameter("personGroupId", enrollmentFront.getPersonGroup().getId())
                    .list()
                    .forEach(transactionalDataManager::remove);
        }
    }

    protected void sendNotifyToTrainers(Enrollment enrollment) {
        List<CourseTrainer> courseTrainerList = enrollment.getCourse() != null
                ? enrollment.getCourse().getCourseTrainers() : null;
        if (courseTrainerList != null && !courseTrainerList.isEmpty()) {
            courseTrainerList.forEach(courseTrainer -> {
                TsadvUser tsadvUserTrainer = dataManager.load(TsadvUser.class)
                        .query("select e from tsadv$UserExt e " +
                                " join tsadv$CourseTrainer ct on ct.trainer.employee.id = e.personGroup.id " +
                                " where ct.id = :courseTrainerId " +
                                " and ( ct.trainer.company.code = 'empty'  " +
                                "  or ct.trainer.company.id in " +
                                " (select en.personGroup.company.id from tsadv$Enrollment en " +
                                " where en.id = :enrollmentId  ) )")
                        .parameter("courseTrainerId", courseTrainer.getId())
                        .parameter("enrollmentId", enrollment.getId())
                        .view("userExt.edit")
                        .list().stream().findFirst().orElse(null);
                if (tsadvUserTrainer != null) {
                    Person trainer = courseTrainer.getTrainer() != null
                            && courseTrainer.getTrainer().getEmployee() != null
                            ? courseTrainer.getTrainer().getEmployee().getPerson()
                            : null;
                    Person student = enrollment.getPersonGroup() != null ? enrollment.getPersonGroup().getPerson() : null;
                    if (trainer != null && student != null) {
                        Map<String, Object> params = new HashMap<>();
                        params.put("trainerFioRu", trainer.getFirstName() + " " + trainer.getLastName());
                        params.put("trainerFioEn", trainer.getFirstNameLatin() != null
                                && !trainer.getFirstNameLatin().isEmpty()
                                && trainer.getLastNameLatin() != null
                                && !trainer.getLastNameLatin().isEmpty()
                                ? trainer.getFirstNameLatin() + " " + trainer.getLastNameLatin()
                                : trainer.getFirstName() + " " + trainer.getLastName());
                        params.put("studentFioRu", student.getFirstName() + " " + student.getLastName());
                        params.put("studentFioEn", student.getFirstNameLatin() != null
                                && !student.getFirstNameLatin().isEmpty()
                                && student.getLastNameLatin() != null
                                && !student.getLastNameLatin().isEmpty()
                                ? student.getFirstNameLatin() + " " + student.getLastNameLatin()
                                : student.getFirstName() + " " + student.getLastName());
                        params.put("course", enrollment.getCourse().getName());

                        notificationSenderAPIService.sendParametrizedNotification("tdc.student.completed.study",
                                tsadvUserTrainer, params);
                    }
                }
            });
        }
    }

    protected void sendNotifyForLineManager(Enrollment enrollment) {
        organizationHrUserService.getHrUsersForPerson(enrollment.getPersonGroup().getId(), OrganizationHrUserService.HR_ROLE_MANAGER)
                .stream()
                .map(user -> (TsadvUser) user)
                .forEach(tsadvUser -> {
                    tsadvUser = dataManager.reload(tsadvUser, "tsadvUserExt-view");
                    Map<String, Object> params = new HashMap<>();
                    Person user = tsadvUser.getPersonGroup() != null ? tsadvUser.getPersonGroup().getPerson() : null;
                    Person employee = enrollment.getPersonGroup() != null ? enrollment.getPersonGroup().getPerson() : null;
                    if (user != null && employee != null) {
                        params.put("personFioRu", user.getFirstName() + " " + user.getLastName());
                        params.put("personFioEn", user.getFirstNameLatin() != null
                                && !user.getFirstNameLatin().isEmpty()
                                && user.getLastNameLatin() != null
                                && !user.getLastNameLatin().isEmpty()
                                ? user.getFirstNameLatin() + " " + user.getLastNameLatin()
                                : user.getFirstName() + " " + user.getLastName());
                        params.put("employeeFioRu", employee.getFirstName() + " " + employee.getLastName());
                        params.put("employeeFioEn", employee.getFirstNameLatin() != null
                                && !employee.getFirstNameLatin().isEmpty()
                                && employee.getLastNameLatin() != null
                                && !employee.getLastNameLatin().isEmpty()
                                ? employee.getFirstNameLatin() + " " + employee.getLastNameLatin()
                                : employee.getFirstName() + " " + employee.getLastName());
                        params.put("course", enrollment.getCourse().getName());
                        notificationSenderAPIService.sendParametrizedNotification("tdc.employee.completed.study",
                                tsadvUser, params);
                    }
                });
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void afterCommit(EntityChangedEvent<Enrollment, UUID> event) {
//        AttributeChanges changes = event.getChanges();
//        Id<Enrollment, UUID> entityId = event.getEntityId();
//        Enrollment enrollment = dataManager.load(entityId).view("enrollment.for.course").one();
//        if (event.getType().equals(EntityChangedEvent.Type.UPDATED)) {
//
//            for (String attribute : changes.getAttributes()) {
//
//                if (!attribute.equals("status")) continue;
//
//                Integer oldValue = changes.getOldValue(attribute);
//                if (!EnrollmentStatus.APPROVED.equals(EnrollmentStatus.fromId(oldValue))
//                        && EnrollmentStatus.APPROVED.equals(enrollment.getStatus())) {
//
//                    TsadvUser tsadvUser = dataManager.load(TsadvUser.class)
//                            .query("select e from tsadv$UserExt e " +
//                                    " where e.personGroup = :personGroup")
//                            .parameter("personGroup", enrollment.getPersonGroup())
//                            .list().stream().findFirst().orElse(null);
//                    if (tsadvUser != null) {
//                        Map<String, Object> map = new HashMap<>();
//                        String courseLink = "<a href=\"" + frontConfig.getFrontAppUrl()
//                                + "/course/"
//                                + "\" target=\"_blank\">%s " + "</a>";
//                        String myCourseLink = "<a href=\"" + frontConfig.getFrontAppUrl()
//                                + "/my-course/"
//                                + "\" target=\"_blank\">%s " + "</a>";
//                        map.put("linkRu", String.format(courseLink, enrollment.getCourse().getName()));
//                        map.put("linkEn", String.format(courseLink, enrollment.getCourse().getName()));
//                        map.put("linkKz", String.format(courseLink, enrollment.getCourse().getName()));
//                        map.put("myLinkRu", String.format(myCourseLink, "Мои курсы"));
//                        map.put("myLinkEn", String.format(myCourseLink, "My training course"));
//                        map.put("myLinkKz", String.format(myCourseLink, "Менің курстарым"));
//                        map.put("personFullName", enrollment.getPersonGroup().getFirstLastName());
//                        map.put("courseName", enrollment.getCourse().getName());
//                        activityService.createActivity(
//                                tsadvUser,
//                                tsadvUser,
//                                getActivityType(),
//                                StatusEnum.active,
//                                "description",
//                                null,
//                                new Date(),
//                                null,
//                                null,
//                                enrollment.getId(),
//                                "tdc.student.enrollmentApproved",
//                                map);
//                        notificationSenderAPIService.sendParametrizedNotification("tdc.student.enrollmentApproved",
//                                tsadvUser, map);
//                    }
//
//                } else if (!EnrollmentStatus.COMPLETED.equals(EnrollmentStatus.fromId(oldValue))
//                        && EnrollmentStatus.COMPLETED.equals(enrollment.getStatus())) {
//
//                    TsadvUser user = dataManager.load(TsadvUser.class)
//                            .query("select e from tsadv$UserExt e " +
//                                    " where e.personGroup = :personGroup")
//                            .parameter("personGroup", enrollment.getPersonGroup())
//                            .list().stream().findFirst().orElse(null);
//                    Map<String, Object> map = new HashMap<>();
//                    String requestLink = "<a href=\"" + frontConfig.getFrontAppUrl()
//                            + "/learning-history/"
//                            + "\" target=\"_blank\">%s " + "</a>";
//                    map.put("linkRu", String.format(requestLink, "ссылке"));
//                    map.put("linkEn", String.format(requestLink, "link"));
//                    map.put("linkKz", String.format(requestLink, "сілтеме"));
//                    map.put("courseName", enrollment.getCourse().getName());
//                    map.put("personFullName", enrollment.getPersonGroup().getFirstLastName());
//
////                    sendNotificationCompleted(enrollment);
//                    CommitContext commitContext = new CommitContext();
//                    CourseCertificate courseCertificate = enrollment.getCourse().getCertificate() != null
//                            && !enrollment.getCourse().getCertificate().isEmpty()
//                            ? enrollment.getCourse().getCertificate().get(0)
//                            : null;
//                    if (courseCertificate != null) {
//
//                        FileDescriptor fd = reportService.createAndSaveReport(courseCertificate.getCertificate(),
//                                ParamsMap.of("enrollment", enrollment), enrollment.getCourse().getName());
//
//
//                        if (fd != null) {
//                            List<EnrollmentCertificateFile> ecfList = dataManager.load(EnrollmentCertificateFile.class)
//                                    .query("select e from tsadv$EnrollmentCertificateFile e " +
//                                            " where e.enrollment = :enrollment ")
//                                    .parameter("enrollment", enrollment)
//                                    .view("enrollmentCertificateFile.with.certificateFile")
//                                    .list();
//                            ecfList.forEach(commitContext::addInstanceToRemove);
//
//                            EnrollmentCertificateFile ecf = metadata.create(EnrollmentCertificateFile.class);
//                            ecf.setCertificateFile(fd);
//                            ecf.setEnrollment(enrollment);
//                            commitContext.addInstanceToCommit(ecf);
//
//                            dataManager.commit(commitContext);
//
//                            EmailAttachment[] emailAttachments = new EmailAttachment[0];
//                            emailAttachments = getEmailAttachments(fd, emailAttachments);
//                            activityService.createActivity(
//                                    user,
//                                    user,
//                                    getActivityType(),
//                                    StatusEnum.active,
//                                    "description",
//                                    null,
//                                    new Date(),
//                                    null,
//                                    null,
//                                    enrollment.getId(),
//                                    "tdc.student.enrollmentClosed",
//                                    map);
//
//                            notificationSenderAPIService.sendParametrizedNotification("tdc.student.enrollmentClosed",
//                                    user, map, emailAttachments);
//                        } else {
//                            activityService.createActivity(
//                                    user,
//                                    user,
//                                    getActivityType(),
//                                    StatusEnum.active,
//                                    "description",
//                                    null,
//                                    new Date(),
//                                    null,
//                                    null,
//                                    enrollment.getId(),
//                                    "tdc.student.enrollmentClosed",
//                                    map);
//                            notificationSenderAPIService.sendParametrizedNotification("tdc.student.enrollmentClosed",
//                                    user, map);
//                        }
//                    } else {
//                        activityService.createActivity(
//                                user,
//                                user,
//                                getActivityType(),
//                                StatusEnum.active,
//                                "description",
//                                null,
//                                new Date(),
//                                null,
//                                null,
//                                enrollment.getId(),
//                                "tdc.student.enrollmentClosed",
//                                map);
//                        notificationSenderAPIService.sendParametrizedNotification("tdc.student.enrollmentClosed",
//                                user, map);
//                    }
//                }
//            }
//        } else if (event.getType().equals(EntityChangedEvent.Type.CREATED)) {
//
//            if (EnrollmentStatus.APPROVED.equals(enrollment.getStatus())) {
//
//                TsadvUser tsadvUser = dataManager.load(TsadvUser.class)
//                        .query("select e from tsadv$UserExt e " +
//                                " where e.personGroup = :personGroup")
//                        .parameter("personGroup", enrollment.getPersonGroup())
//                        .list().stream().findFirst().orElse(null);
//                if (tsadvUser != null) {
//                    Map<String, Object> map = new HashMap<>();
//                    String courseLink = "<a href=\"" + frontConfig.getFrontAppUrl()
//                            + "/course/"
//                            + "\" target=\"_blank\">%s " + "</a>";
//                    String myCourseLink = "<a href=\"" + frontConfig.getFrontAppUrl()
//                            + "/my-course/"
//                            + "\" target=\"_blank\">%s " + "</a>";
//                    map.put("linkRu", String.format(courseLink, enrollment.getCourse().getName()));
//                    map.put("linkEn", String.format(courseLink, enrollment.getCourse().getName()));
//                    map.put("linkKz", String.format(courseLink, enrollment.getCourse().getName()));
//                    map.put("myLinkRu", String.format(myCourseLink, "Мои курсы"));
//                    map.put("myLinkEn", String.format(myCourseLink, "My training course"));
//                    map.put("myLinkKz", String.format(myCourseLink, "Менің курстарым"));
//                    map.put("personFullName", enrollment.getPersonGroup().getFirstLastName());
//                    map.put("courseName", enrollment.getCourse().getName());
//                    activityService.createActivity(
//                            tsadvUser,
//                            tsadvUser,
//                            getActivityType(),
//                            StatusEnum.active,
//                            "description",
//                            null,
//                            new Date(),
//                            null,
//                            null,
//                            enrollment.getId(),
//                            "tdc.student.enrollmentApproved",
//                            map);
//                    notificationSenderAPIService.sendParametrizedNotification("tdc.student.enrollmentApproved",
//                            tsadvUser, map);
//                }
//            } else {
//                sendNotification(enrollment, "tdc.trainer.newEnrollment");
//            }
//        }

    }

//    protected boolean generateCertificate(Enrollment enrollment) {
//        boolean success = true;
//        if (enrollment.getCourse().getSelfEnrollment()) {
//            for (CourseSection section : enrollment.getCourse().getSections()) {
//                List<CourseSectionAttempt> courseSectionAttemptList = dataManager.load(CourseSectionAttempt.class)
//                        .query("select e from tsadv$CourseSectionAttempt e " +
//                                " where e.success = true " +
//                                " and e.courseSection = :section")
//                        .parameter("section", section)
//                        .view("courseSectionAttempt.edit")
//                        .list();
//                if (courseSectionAttemptList.size() < 1) {
//                    success = false;
//                }
//            }
//        }
//        return success;
//    }

    protected void sendNotification(Enrollment enrollment, String notificationCode) {
        List<CourseTrainer> courseTrainers = dataManager.load(CourseTrainer.class)
                .query("select e from tsadv$CourseTrainer e " +
                        " where e.trainer.employee is not null " +
                        " and (e.trainer.company.code = 'empty' " +
                        " or e.trainer.company.id in " +
                        " (select en.personGroup.company.id from tsadv$Enrollment en " +
                        " where en.id = :enrollmentId and en.course.id = e.course.id ))")
                .parameter("enrollmentId", enrollment.getId())
                .view("courseTrainer.edit")
                .list();

        if (!courseTrainers.isEmpty()) {
            courseTrainers.forEach(courseTrainer -> {
                TsadvUser tsadvUser = dataManager.load(TsadvUser.class)
                        .query("select e from tsadv$UserExt e " +
                                " where e.personGroup = :personGroup")
                        .parameter("personGroup", courseTrainer.getTrainer().getEmployee())
                        .list().stream().findFirst().orElse(null);
                if (tsadvUser != null) {
                    Map<String, Object> map = new HashMap<>();
                    Person trainer = courseTrainer.getTrainer() != null
                            && courseTrainer.getTrainer().getEmployee() != null
                            ? courseTrainer.getTrainer().getEmployee().getPerson()
                            : null;
                    Person person = enrollment.getPersonGroup() != null ? enrollment.getPersonGroup().getPerson() : null;
                    if (trainer != null && person != null) {
                        String requestLink = "<a href=\"" + globalConfig.getWebAppUrl() + "/open?screen=" +
                                "tsadv$Course.edit" +
                                "&item=" + "tsadv$Course" + "-" + courseTrainer.getCourse().getId() +
                                "\" target=\"_blank\">%s " + "</a>";
                        map.put("courseName", enrollment.getCourse().getName());
                        map.put("trainerFullNameRu", trainer.getFirstName() + " " + trainer.getLastName());
                        map.put("trainerFullNameEn", trainer.getFirstNameLatin() != null
                                && !trainer.getFirstNameLatin().isEmpty()
                                && trainer.getLastNameLatin() != null
                                && !trainer.getLastNameLatin().isEmpty()
                                ? trainer.getFirstNameLatin() + " " + trainer.getLastNameLatin()
                                : trainer.getFirstName() + " " + trainer.getLastName());
                        map.put("personFullNameRu", person.getFirstName() + " " + person.getLastName());
                        map.put("personFullNameEn", person.getFirstNameLatin() != null
                                && !person.getFirstNameLatin().isEmpty()
                                && person.getLastNameLatin() != null
                                && !person.getLastNameLatin().isEmpty()
                                ? person.getFirstNameLatin() + " " + person.getLastNameLatin()
                                : person.getFirstName() + " " + person.getLastName());
                        map.put("linkRu", String.format(requestLink, "курс"));
                        map.put("linkKz", String.format(requestLink, "курсыңызға"));
                        map.put("linkEn", String.format(requestLink, "course"));
                        activityService.createActivity(
                                tsadvUser,
                                tsadvUser,
                                getActivityType(),
                                StatusEnum.active,
                                "description",
                                null,
                                new Date(),
                                null,
                                null,
                                courseTrainer.getId(),
                                notificationCode,
                                map);
                        notificationSenderAPIService.sendParametrizedNotification(notificationCode,
                                tsadvUser, map);
                    }
                }
            });
        }
    }

    protected ActivityType getActivityType() {
        return dataManager.load(ActivityType.class)
                .query("select e from uactivity$ActivityType e where e.code = 'NOTIFICATION'")
                .view(new View(ActivityType.class)
                        .addProperty("code")
                        .addProperty("windowProperty",
                                new View(WindowProperty.class).addProperty("entityName").addProperty("screenName")))
                .one();
    }

//    protected void sendNotificationCompleted(Enrollment enrollment) {
//        TsadvUser user = dataManager.load(TsadvUser.class)
//                .query("select e from tsadv$UserExt e " +
//                        " where e.personGroup = :personGroup")
//                .parameter("personGroup", enrollment.getPersonGroup())
//                .list().stream().findFirst().orElse(null);
//        if (user != null) {
//
//            UUID fileDescriptorId = UUID.fromString(lmsService.getCertificate(String.valueOf(enrollment.getId())));
//
//            FileDescriptor fileDescriptor = dataManager.load(FileDescriptor.class)
//                    .query("select e from SYS_FILE e " +
//                            " where e.id = :fileDescriptorId")
//                    .parameter("fileDescriptorId", fileDescriptorId)
//                    .list().stream().findFirst().orElse(null);
//
//            Map<String, Object> map = new HashMap<>();
//            map.put("courseName", enrollment.getCourse().getName());
//
//            if (fileDescriptor != null) {
//                EmailAttachment[] emailAttachments = new EmailAttachment[0];
//                emailAttachments = getEmailAttachments(fileDescriptor, emailAttachments);
//                notificationSenderAPIService.sendParametrizedNotification("tdc.student.enrollmentClosed",
//                        user, map, emailAttachments);
//            } else {
//                notificationSenderAPIService.sendParametrizedNotification("tdc.student.enrollmentClosed",
//                        user, map);
//            }
//        }
//    }

    protected EmailAttachment[] getEmailAttachments(FileDescriptor fileDescriptor, EmailAttachment[] emailAttachments) {
        try {
            EmailAttachment emailAttachment = new EmailAttachment(fileStorageAPI.loadFile(fileDescriptor), "Сертификат.pdf");
            emailAttachments = ArrayUtils.add(emailAttachments, emailAttachment);
        } catch (FileStorageException e) {
            e.printStackTrace();
        }
        return emailAttachments;
    }
}