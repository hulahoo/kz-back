package kz.uco.tsadv.listeners;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.TransactionalDataManager;
import com.haulmont.cuba.core.app.FileStorageAPI;
import com.haulmont.cuba.core.app.events.EntityChangedEvent;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.contracts.Id;
import com.haulmont.cuba.core.global.*;
import com.haulmont.reports.app.service.ReportService;
import kz.uco.base.service.NotificationSenderAPIService;
import kz.uco.tsadv.config.FrontConfig;
import kz.uco.tsadv.modules.administration.TsadvUser;
import kz.uco.tsadv.modules.learning.model.*;
import kz.uco.tsadv.modules.learning.model.feedback.CourseFeedbackPersonAnswer;
import kz.uco.tsadv.modules.performance.model.CourseTrainer;
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

@Component("tsadv_CourseFeedbackPersonAnswerChangedListener")
public class CourseFeedbackPersonAnswerChangedListener {

    @Inject
    protected DataManager dataManager;
    @Inject
    protected LearningService learningService;
    @Inject
    protected TransactionalDataManager transactionalDataManager;
    @Inject
    private NotificationSenderAPIService notificationSenderAPIService;
    @Inject
    private OrganizationHrUserService organizationHrUserService;
    @Inject
    private FileStorageAPI fileStorageAPI;
    @Inject
    private FrontConfig frontConfig;
    @Inject
    private ReportService reportService;
    @Inject
    private Metadata metadata;
    @Inject
    private ActivityService activityService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void beforeCommit(EntityChangedEvent<CourseFeedbackPersonAnswer, UUID> event) {
        Id<CourseFeedbackPersonAnswer, UUID> entityId = event.getEntityId();
        CourseFeedbackPersonAnswer courseFeedbackPersonAnswer;
        if (event.getType().equals(EntityChangedEvent.Type.CREATED)) {
//            courseFeedbackPersonAnswer = transactionalDataManager.load(entityId).view("courseFeedbackPersonAnswer.edit").one();
//            Enrollment enrollment = transactionalDataManager.load(Enrollment.class)
//                    .query("select e from tsadv$Enrollment e " +
//                            " where e.personGroup = :personGroup " +
//                            " and e.course = :course")
//                    .parameter("personGroup", courseFeedbackPersonAnswer.getPersonGroup())
//                    .parameter("course", courseFeedbackPersonAnswer.getCourse())
//                    .view("enrollment-view")
//                    .list().stream().findFirst().orElse(null);
//            if (learningService.allCourseSectionPassed(courseFeedbackPersonAnswer.getCourse() != null
//                    ? courseFeedbackPersonAnswer.getCourse().getSections()
//                    : null, enrollment)
//                    && learningService.haveAFeedbackQuestion(courseFeedbackPersonAnswer.getCourse().getFeedbackTemplates(),
//                    courseFeedbackPersonAnswer.getPersonGroup())) {
//                boolean homework = true;
//                List<Homework> homeworkList = getHomeworkForCourse(courseFeedbackPersonAnswer.getCourse());
//                if (!homeworkList.isEmpty()) {
//                    homework = learningService.allHomeworkPassed(homeworkList, courseFeedbackPersonAnswer.getPersonGroup());
//                }
//                if (enrollment != null && homework) {
//                    enrollment.setStatus(EnrollmentStatus.COMPLETED);
//                    transactionalDataManager.save(enrollment);
//                    sendNotifyToTrainers(enrollment);
//                    sendNotifyForLineManager(enrollment);
//                }
//            }
        } else if (event.getType().equals(EntityChangedEvent.Type.UPDATED)) {
//            courseFeedbackPersonAnswer = dataManager.load(entityId).view("courseFeedbackPersonAnswer.edit").one();
//            Enrollment enrollment = transactionalDataManager.load(Enrollment.class)
//                    .query("select e from tsadv$Enrollment e " +
//                            " where e.personGroup = :personGroup " +
//                            " and e.course = :course")
//                    .parameter("personGroup", courseFeedbackPersonAnswer.getPersonGroup())
//                    .parameter("course", courseFeedbackPersonAnswer.getCourse())
//                    .view("enrollment-view")
//                    .list().stream().findFirst().orElse(null);
//            if (learningService.allCourseSectionPassed(courseFeedbackPersonAnswer.getCourse() != null
//                    ? courseFeedbackPersonAnswer.getCourse().getSections()
//                    : null, enrollment)
//                    && learningService.haveAFeedbackQuestion(courseFeedbackPersonAnswer.getCourse().getFeedbackTemplates(),
//                    courseFeedbackPersonAnswer.getPersonGroup())) {
//                boolean homework = true;
//                List<Homework> homeworkList = getHomeworkForCourse(courseFeedbackPersonAnswer.getCourse());
//                if (!homeworkList.isEmpty()) {
//                    homework = learningService.allHomeworkPassed(homeworkList, courseFeedbackPersonAnswer.getPersonGroup());
//                }
//                if (enrollment != null && homework) {
//                    enrollment.setStatus(EnrollmentStatus.COMPLETED);
//                    transactionalDataManager.save(enrollment);
//                    sendNotifyToTrainers(enrollment);
//                    sendNotifyForLineManager(enrollment);
//                }
//            }
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

    protected void sendNotifyToTrainers(Enrollment enrollment) {
        List<CourseTrainer> courseTrainerList = enrollment.getCourse() != null
                ? enrollment.getCourse().getCourseTrainers() : null;
        if (courseTrainerList != null && !courseTrainerList.isEmpty()) {
            courseTrainerList.forEach(courseTrainer -> {
                TsadvUser tsadvUserTrainer = dataManager.load(TsadvUser.class)
                        .query("select e from tsadv$UserExt e " +
                                " where e.personGroup = :personGroup ")
                        .parameter("personGroup", courseTrainer.getTrainer() != null
                                ? courseTrainer.getTrainer().getEmployee() : null)
                        .view("userExt.edit")
                        .list().stream().findFirst().orElse(null);
                if (tsadvUserTrainer != null) {
                    Map<String, Object> params = new HashMap<>();
                    params.put("trainerFioRu", courseTrainer.getTrainer().getEmployee() != null
                            ? courseTrainer.getTrainer().getEmployee().getFullName() : "");
                    params.put("trainerFioEn", courseTrainer.getTrainer().getEmployee() != null
                            ? courseTrainer.getTrainer().getEmployee().getPersonFirstLastNameLatin()
                            : "");
                    params.put("studentFioRu", enrollment.getPersonGroup() != null
                            ? enrollment.getPersonGroup().getFullName() : "");
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
                            ? tsadvUser.getPersonGroup().getFullName() : "");
                    params.put("personFioEn", tsadvUser.getPersonGroup() != null
                            ? tsadvUser.getPersonGroup().getPersonFirstLastNameLatin() : "");
                    params.put("employeeFioRu", enrollment.getPersonGroup().getFullName());
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
        map.put("linkRu", String.format(requestLink, "История обучения"));
        map.put("linkEn", String.format(requestLink, "Training History"));
        map.put("linkKz", String.format(requestLink, "Оқу үлгерімі"));
        map.put("courseName", enrollment.getCourse().getName());
        map.put("personFullName", enrollment.getPersonGroup().getFirstLastName());

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

    protected ActivityType getActivityType() {
        return dataManager.load(ActivityType.class)
                .query("select e from uactivity$ActivityType e where e.code = 'NOTIFICATION'")
                .view(new View(ActivityType.class)
                        .addProperty("code")
                        .addProperty("windowProperty",
                                new View(WindowProperty.class).addProperty("entityName").addProperty("screenName")))
                .one();
    }
}