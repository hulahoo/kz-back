package kz.uco.tsadv.listener;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.TransactionalDataManager;
import com.haulmont.cuba.core.app.FileStorageAPI;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.core.listener.*;
import com.haulmont.reports.app.service.ReportService;
import kz.uco.base.service.NotificationSenderAPIService;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.beans.TestHelper;
import kz.uco.tsadv.config.FrontConfig;
import kz.uco.tsadv.modules.administration.TsadvUser;
import kz.uco.tsadv.modules.learning.enums.EnrollmentStatus;
import kz.uco.tsadv.modules.learning.model.*;
import kz.uco.tsadv.modules.learning.model.feedback.CourseFeedbackTemplate;
import kz.uco.tsadv.modules.performance.model.CourseTrainer;
import kz.uco.tsadv.service.LearningService;
import kz.uco.tsadv.service.OrganizationHrUserService;
import kz.uco.uactivity.entity.ActivityType;
import kz.uco.uactivity.entity.StatusEnum;
import kz.uco.uactivity.entity.WindowProperty;
import kz.uco.uactivity.service.ActivityService;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.sql.Connection;
import java.util.*;

@Component("tsadv_CourseSectionAttemptListener")
public class CourseSectionAttemptListener implements BeforeDeleteEntityListener<CourseSectionAttempt>, BeforeDetachEntityListener<CourseSectionAttempt>, BeforeInsertEntityListener<CourseSectionAttempt>, BeforeUpdateEntityListener<CourseSectionAttempt>, AfterDeleteEntityListener<CourseSectionAttempt>, AfterInsertEntityListener<CourseSectionAttempt>, AfterUpdateEntityListener<CourseSectionAttempt>, BeforeAttachEntityListener<CourseSectionAttempt> {

    @Inject
    protected DataManager dataManager;

    @Inject
    protected Metadata metadata;

    @Inject
    protected CommonService commonService;
    @Inject
    protected LearningService learningService;
    @Inject
    protected TransactionalDataManager transactionalDataManager;
    @Inject
    protected FrontConfig frontConfig;
    @Inject
    protected ReportService reportService;
    @Inject
    protected ActivityService activityService;
    @Inject
    protected FileStorageAPI fileStorageAPI;
    @Inject
    protected NotificationSenderAPIService notificationSenderAPIService;
    @Inject
    private OrganizationHrUserService organizationHrUserService;

    @Override
    public void onBeforeDelete(CourseSectionAttempt courseSectionAttempt, EntityManager entityManager) {

    }


    @Override
    public void onBeforeDetach(CourseSectionAttempt courseSectionAttempt, EntityManager entityManager) {

    }


    @Override
    public void onBeforeInsert(CourseSectionAttempt courseSectionAttempt, EntityManager entityManager) {
        if (courseSectionAttempt.getTest() != null) {
            courseSectionAttempt.setTestResultPercent(testHelper.calculateTestResultPercent(courseSectionAttempt));
        }
        CourseSection courseSection = entityManager.find(CourseSection.class, courseSectionAttempt.getCourseSection().getId(), "courseSection.edit");
        if (courseSection != null && courseSection.getCourse() != null
                && courseSection.getCourse().getSections() != null) {
            courseSection = dataManager.reload(courseSection, "courseSection.edit");
            UUID enrollmentId = courseSectionAttempt.getEnrollment().getId();
            Enrollment enrollment = entityManager.find(Enrollment.class, enrollmentId, "enrollment.for.course");
            if (enrollment != null) {
                enrollment = dataManager.reload(enrollment, "enrollment.for.course");
                if (learningService.allCourseSectionPassed(courseSection.getCourse().getSections(),
                        enrollment)) {
                    boolean homework = true;
                    boolean feedbackQuestion = true;
                    List<CourseFeedbackTemplate> courseFeedbackTemplateList = courseSection.getCourse().getFeedbackTemplates();
                    if (!courseFeedbackTemplateList.isEmpty()) {
                        feedbackQuestion = learningService.haveAFeedbackQuestion(courseFeedbackTemplateList, enrollment.getPersonGroup());
                    }
                    List<Homework> homeworkList = getHomeworkForCourse(courseSection.getCourse());
                    if (!homeworkList.isEmpty()) {
                        homework = learningService.allHomeworkPassed(homeworkList, enrollment.getPersonGroup());
                    }
                    if (homework && feedbackQuestion) {
                        enrollment.setStatus(EnrollmentStatus.COMPLETED);
                        transactionalDataManager.save(enrollment);

                        sendNotification(enrollment);
                        sendNotifyToTrainers(enrollment);
                        sendNotifyForLineManager(enrollment);
                    }
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


    @Override
    public void onBeforeUpdate(CourseSectionAttempt courseSectionAttempt, EntityManager entityManager) {
        if (courseSectionAttempt.getTest() != null &&
                persistence.getTools().isDirty(courseSectionAttempt, "testResult")
                && courseSectionAttempt.getTestResultPercent() == null
        ) {
            courseSectionAttempt.setTestResultPercent(testHelper.calculateTestResultPercent(courseSectionAttempt));
        }
        CourseSection courseSection = entityManager.find(CourseSection.class, courseSectionAttempt.getCourseSection().getId(), "courseSection.edit");
        if (courseSection != null && courseSection.getCourse() != null
                && courseSection.getCourse().getSections() != null) {
            courseSection = dataManager.reload(courseSection, "courseSection.edit");
            UUID enrollmentId = courseSectionAttempt.getEnrollment().getId();
            Enrollment enrollment = entityManager.find(Enrollment.class, enrollmentId, "enrollment.person");
            if (enrollment != null) {
                enrollment = dataManager.reload(enrollment, "enrollment.person");
                if (learningService.allCourseSectionPassed(courseSection.getCourse().getSections(),
                        enrollment)) {
                    boolean homework = true;
                    boolean feedbackQuestion = true;
                    List<CourseFeedbackTemplate> courseFeedbackTemplateList = courseSection.getCourse().getFeedbackTemplates();
                    if (!courseFeedbackTemplateList.isEmpty()) {
                        feedbackQuestion = learningService.haveAFeedbackQuestion(courseFeedbackTemplateList, enrollment.getPersonGroup());
                    }
                    List<Homework> homeworkList = getHomeworkForCourse(courseSection.getCourse());
                    if (!homeworkList.isEmpty()) {
                        homework = learningService.allHomeworkPassed(homeworkList, enrollment.getPersonGroup());
                    }
                    if (homework && feedbackQuestion) {
                        enrollment.setStatus(EnrollmentStatus.COMPLETED);
                        transactionalDataManager.save(enrollment);

                        sendNotification(enrollment);
                        sendNotifyToTrainers(enrollment);
                        sendNotifyForLineManager(enrollment);
                    }
                }
            }
        }
    }

    protected void sendNotification(Enrollment enrollment) {
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
        List<TsadvUser> lineManagerList = (List<TsadvUser>) organizationHrUserService.getHrUsersForPerson(enrollment.getPersonGroup().getId(), "HR_ROLE_MANAGER");
        lineManagerList.forEach(tsadvUser -> {
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


    @Override
    public void onAfterDelete(CourseSectionAttempt courseSectionAttempt, Connection connection) {

    }


    @Override
    public void onAfterInsert(CourseSectionAttempt courseSectionAttempt, Connection connection) {

    }


    @Override
    public void onAfterUpdate(CourseSectionAttempt courseSectionAttempt, Connection connection) {

    }


    @Override
    public void onBeforeAttach(CourseSectionAttempt courseSectionAttempt) {

    }

    @Inject
    protected Persistence persistence;
    @Inject
    protected TestHelper testHelper;

}