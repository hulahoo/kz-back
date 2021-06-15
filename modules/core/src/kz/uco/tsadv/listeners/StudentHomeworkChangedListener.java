package kz.uco.tsadv.listeners;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.TransactionalDataManager;
import com.haulmont.cuba.core.app.FileStorageAPI;
import com.haulmont.cuba.core.app.events.AttributeChanges;
import com.haulmont.cuba.core.app.events.EntityChangedEvent;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.contracts.Id;
import com.haulmont.cuba.core.global.*;
import com.haulmont.reports.app.service.ReportService;
import kz.uco.base.common.BaseCommonUtils;
import kz.uco.base.service.NotificationSenderAPIService;
import kz.uco.tsadv.config.FrontConfig;
import kz.uco.tsadv.modules.administration.TsadvUser;
import kz.uco.tsadv.modules.learning.enums.EnrollmentStatus;
import kz.uco.tsadv.modules.learning.model.*;
import kz.uco.tsadv.modules.performance.model.CourseTrainer;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.service.LearningService;
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

@Component("tsadv_StudentHomeworkChangedListener")
public class StudentHomeworkChangedListener {

    @Inject
    protected DataManager dataManager;

    @Inject
    protected NotificationSenderAPIService notificationSenderAPIService;
    @Inject
    protected GlobalConfig globalConfig;
    @Inject
    protected LearningService learningService;
    @Inject
    protected TransactionalDataManager transactionalDataManager;
    @Inject
    protected ActivityService activityService;
    @Inject
    protected FrontConfig frontConfig;
    @Inject
    private OrganizationHrUserService organizationHrUserService;
    @Inject
    private FileStorageAPI fileStorageAPI;
    @Inject
    private Metadata metadata;
    @Inject
    private ReportService reportService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onTsadv_StudentHomeworkBeforeCommit(EntityChangedEvent<StudentHomework, UUID> event) {
        Id<StudentHomework, UUID> entityId = event.getEntityId();
        StudentHomework studentHomework;
        if (event.getType().equals(EntityChangedEvent.Type.CREATED)) {
            studentHomework = transactionalDataManager.load(entityId).view("studentHomework.edit").one();
            Enrollment enrollment = transactionalDataManager.load(Enrollment.class)
                    .query("select e from tsadv$Enrollment e " +
                            " where e.personGroup = :personGroup " +
                            " and e.course = :course")
                    .parameter("personGroup", studentHomework.getPersonGroup())
                    .parameter("course", studentHomework.getHomework().getCourse())
                    .view("enrollment-view")
                    .list().stream().findFirst().orElse(null);
            if (learningService.allCourseSectionPassed(studentHomework.getHomework().getCourse() != null
                    ? studentHomework.getHomework().getCourse().getSections()
                    : null, enrollment)
                    && learningService.allHomeworkPassed(getHomeworkForCourse(studentHomework.getHomework().getCourse()),
                    studentHomework.getPersonGroup())) {
//                boolean feedbackQuestion = true;
//                List<CourseFeedbackTemplate> courseFeedbackTemplateList = studentHomework.getHomework().getCourse().getFeedbackTemplates();
//                if (!courseFeedbackTemplateList.isEmpty()) {
//                    feedbackQuestion = learningService.haveAFeedbackQuestion(courseFeedbackTemplateList, studentHomework.getPersonGroup());
//                }
                if (enrollment != null) {
                    enrollment.setStatus(EnrollmentStatus.COMPLETED);
                    transactionalDataManager.save(enrollment);
                    sendNotificationCertificate(enrollment);
                    sendNotifyToTrainers(enrollment);
                    sendNotifyForLineManager(enrollment);
                }
            }
            sendNotification(studentHomework, "tdc.homework.trainer.newHomework", true);
        } else if (event.getType().equals(EntityChangedEvent.Type.UPDATED)) {
            AttributeChanges changes = event.getChanges();
            studentHomework = transactionalDataManager.load(entityId).view("studentHomework.edit").one();
            Enrollment enrollment = transactionalDataManager.load(Enrollment.class)
                    .query("select e from tsadv$Enrollment e " +
                            " where e.personGroup = :personGroup " +
                            " and e.course = :course")
                    .parameter("personGroup", studentHomework.getPersonGroup())
                    .parameter("course", studentHomework.getHomework().getCourse())
                    .view("enrollment-view")
                    .list().stream().findFirst().orElse(null);
            if (learningService.allCourseSectionPassed(studentHomework.getHomework().getCourse() != null
                    ? studentHomework.getHomework().getCourse().getSections()
                    : null, enrollment)
                    && learningService.allHomeworkPassed(getHomeworkForCourse(studentHomework.getHomework().getCourse()),
                    studentHomework.getPersonGroup())) {
//                boolean feedbackQuestion = true;
//                List<CourseFeedbackTemplate> courseFeedbackTemplateList = studentHomework.getHomework().getCourse().getFeedbackTemplates();
//                if (!courseFeedbackTemplateList.isEmpty()) {
//                    feedbackQuestion = learningService.haveAFeedbackQuestion(courseFeedbackTemplateList, studentHomework.getPersonGroup());
//                }

                if (enrollment != null) {
                    enrollment.setStatus(EnrollmentStatus.COMPLETED);
                    transactionalDataManager.save(enrollment);
                    sendNotificationCertificate(enrollment);
                    sendNotifyToTrainers(enrollment);
                    sendNotifyForLineManager(enrollment);
                }
            }

            boolean isSendStudent = false;
            boolean isSendTrainer = false;

            for (String attribute : changes.getAttributes()) {
                if (attribute.equals("answer") || attribute.equals("answerFile")) {
                    if (!isSendStudent) {
                        sendNotification(studentHomework, "tdc.homework.trainer.newHomework", true);
                    }
                    isSendStudent = true;
                } else if (attribute.equals("isDone") || attribute.equals("trainerComment")) {
                    if (!isSendTrainer) {
                        sendNotification(studentHomework, "tdc.homework.student.trainerAnswer", false);
                    }
                    isSendTrainer = true;
                }
            }
        }

    }

    protected List<Homework> getHomeworkForCourse(Course course) {
        return dataManager.load(Homework.class)
                .query("select e from tsadv_Homework e " +
                        " where e.course = :course")
                .parameter("course", course)
                .view("homework.edit")
                .list();
    }


    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void afterCommit(EntityChangedEvent<StudentHomework, UUID> event) {
//        AttributeChanges changes = event.getChanges();
//        Id<StudentHomework, UUID> entityId = event.getEntityId();
//        StudentHomework studentHomework = dataManager.load(entityId).view("studentHomework.edit").one();
//        if (event.getType().equals(EntityChangedEvent.Type.CREATED)) {
//            sendNotification(studentHomework, "tdc.homework.trainer.newHomework", true);
//        } else if (event.getType().equals(EntityChangedEvent.Type.UPDATED)) {
//
//            for (String attribute : changes.getAttributes()) {
//                if (attribute.equals("answer") || attribute.equals("answerFile")) {
//                    sendNotification(studentHomework, "tdc.homework.trainer.newHomework", true);
//                } else if (attribute.equals("isDone") || attribute.equals("trainerComment")) {
//                    sendNotification(studentHomework, "tdc.homework.student.trainerAnswer", false);
//                }
//            }
//        }

    }

    protected void sendNotification(StudentHomework studentHomework, String notificationCode, Boolean isStudentAnswer) {
        if (isStudentAnswer) {
            List<CourseTrainer> courseTrainers = dataManager.load(CourseTrainer.class)
                    .query("select e from tsadv$CourseTrainer e " +
                            " where e.course = :course " +
                            " and e.trainer.employee is not null " +
                            " and current_date between coalesce(e.dateFrom, :startDate) and coalesce(e.dateTo, :endDate) "
                    )
                    .parameter("course", studentHomework.getHomework().getCourse())
                    .parameter("startDate", new Date(20000101))
                    .parameter("endDate", BaseCommonUtils.getEndOfTime())
                    .view("courseTrainer.edit")
                    .list();

            if (!courseTrainers.isEmpty()) {
                courseTrainers.forEach(courseTrainer -> {
                    TsadvUser tsadvUser = getTsadvUser(courseTrainer.getTrainer().getEmployee());
                    if (tsadvUser != null) {
                        Map<String, Object> map = new HashMap<>();
                        String requestLink = "<a href=\"" + globalConfig.getWebAppUrl() + "/open?screen=" +
                                "tsadv_StudentHomework.edit" +
                                "&item=" + "tsadv_StudentHomework" + "-" + studentHomework.getId() +
                                "\" target=\"_blank\">%s " + "</a>";
                        map.put("trainerFullNameRu", courseTrainer.getTrainer().getEmployee().getFirstLastName());
                        map.put("trainerFullNameEn", courseTrainer.getTrainer().getEmployee().getPersonFirstLastNameLatin());
                        map.put("courseName", studentHomework.getHomework().getCourse().getName());
//                        map.put("studentFullNameRu", studentHomework.getPersonGroup().getFirstLastName());
//                        map.put("studentFullNameEn", studentHomework.getPersonGroup().getPersonFirstLastNameLatin());
                        map.put("requestLinkRu", String.format(requestLink, "ссылке"));
                        map.put("requestLinkEn", String.format(requestLink, "click here"));
                        map.put("requestLinkKz", String.format(requestLink, "сілтеме"));

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
                });
            }
        } else {
            TsadvUser tsadvUser = getTsadvUser(studentHomework.getPersonGroup());

            if (tsadvUser != null) {
                Map<String, Object> map = new HashMap<>();
                map.put("courseName", studentHomework.getHomework().getCourse().getName());
                map.put("studentFullNameRu", studentHomework.getPersonGroup().getFirstLastName());
                map.put("studentFullNameEn", studentHomework.getPersonGroup().getPersonFirstLastNameLatin());
//                String requestLink = "<a href=\"" + globalConfig.getWebAppUrl() + "/open?screen=" +
//                        "tsadv_StudentHomework.edit" +
//                        "&item=" + "tsadv_StudentHomework" + "-" + studentHomework.getId() +
//                        "\" target=\"_blank\">%s " + "</a>";
                String requestLink = "<a href=\"" + frontConfig.getFrontAppUrl()
                        + "/learning-history/"
                        + "\" target=\"_blank\">%s " + "</a>";
                map.put("requestLinkRu", String.format(requestLink, "ответом тренера"));
                map.put("requestLinkEn", String.format(requestLink, "reply"));
                map.put("requestLinkKz", String.format(requestLink, "жауабымен"));

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
                        studentHomework.getId(),
                        notificationCode,
                        map);

                notificationSenderAPIService.sendParametrizedNotification(notificationCode,
                        tsadvUser, map);
            }
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

    private TsadvUser getTsadvUser(PersonGroupExt personGroupExt) {
        return dataManager.load(TsadvUser.class)
                .query("select e from tsadv$UserExt e " +
                        " where e.personGroup = :personGroup")
                .parameter("personGroup", personGroupExt)
                .view("userExt.edit")
                .list().stream().findFirst().orElse(null);
    }

    protected void sendNotifyToTrainers(Enrollment enrollment) {
        List<CourseTrainer> courseTrainerList = enrollment.getCourse() != null
                ? enrollment.getCourse().getCourseTrainers() : null;
        if (courseTrainerList != null && !courseTrainerList.isEmpty()) {
            courseTrainerList.forEach(courseTrainer -> {
                TsadvUser tsadvUserTrainer = getTsadvUser(courseTrainer.getTrainer().getEmployee());
                if (tsadvUserTrainer != null) {
                    Map<String, Object> params = new HashMap<>();
                    params.put("trainerFioRu", courseTrainer.getTrainer().getEmployee() != null
                            ? courseTrainer.getTrainer().getEmployee().getFirstLastName() : "");
                    params.put("trainerFioEn", courseTrainer.getTrainer().getEmployee() != null
                            ? courseTrainer.getTrainer().getEmployee().getPersonFirstLastNameLatin()
                            : "");
                    params.put("studentFioRu", enrollment.getPersonGroup() != null
                            ? enrollment.getPersonGroup().getFirstLastName() : "");
                    params.put("studentFioEn", enrollment.getPersonGroup() != null
                            ? enrollment.getPersonGroup().getPersonFirstLastNameLatin()
                            : "");
                    params.put("course", enrollment.getCourse().getName());

                    notificationSenderAPIService.sendParametrizedNotification("tdc.student.completed.study",
                            tsadvUserTrainer, params);
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
                    params.put("personFioRu", tsadvUser.getPersonGroup() != null
                            ? tsadvUser.getPersonGroup().getFirstLastName() : "");
                    params.put("personFioEn", tsadvUser.getPersonGroup() != null
                            ? tsadvUser.getPersonGroup().getPersonFirstLastNameLatin() : "");
                    params.put("employeeFioRu", enrollment.getPersonGroup().getFirstLastName());
                    params.put("employeeFioEn", enrollment.getPersonGroup().getPersonFirstLastNameLatin());
                    params.put("course", enrollment.getCourse().getName());
                    notificationSenderAPIService.sendParametrizedNotification("tdc.employee.completed.study",
                            tsadvUser, params);
                });
    }

    protected void sendNotificationCertificate(Enrollment enrollment) {
        TsadvUser user = dataManager.load(TsadvUser.class)
                .query("select e from tsadv$UserExt e " +
                        " where e.personGroup = :personGroup")
                .parameter("personGroup", enrollment.getPersonGroup())
                .list().stream().findFirst().orElse(null);
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
        map.put("personFullNameRu", enrollment.getPersonGroup().getFirstLastName());
        map.put("personFullNameEn", enrollment.getPersonGroup().getPersonFirstLastNameLatin());

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
    }

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